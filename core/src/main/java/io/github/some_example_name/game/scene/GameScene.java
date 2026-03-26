package io.github.some_example_name.game.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.CollisionShape;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.CellFactory;
import io.github.some_example_name.game.entity.GameEntity;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.ChemoManager;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.WaveManager;

public class GameScene extends AbstractScene {
    private static final float CHEMO_ACTIVATION_SPREAD = 40f;
    private static final float WORLD_WIDTH = 2000f;
    private static final float WORLD_HEIGHT = 2000f;
    private static final float WORLD_MARGIN = 64f;
    private static final float WIN_TIME_SECONDS = 180f;
    private static final int MAX_ACTIVE_TCELLS = 10;
    private static final int MAX_NORMAL_WAVES = 3;
    private static final float INTERACTION_QUERY_PADDING = 96f;
    private static final int SPAWN_ATTEMPTS = 24;
    private static final float SPAWN_PADDING = 12f;
    private static final float NORMAL_CELL_MIN_PLAYER_DISTANCE = 140f;
    private static final float TCELL_MIN_PLAYER_DISTANCE = 220f;
    private static final float TCELL_SEPARATION_RADIUS = 45f;
    private static final float TCELL_SEPARATION_STRENGTH = 50f;

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private final WaveManager waveManager;
    private final CancerEvolutionManager cancerManager;
    private final ChemoManager chemoManager;
    private final GameInteractionSystem interactionSystem;

    private GameHudRenderer hudRenderer;
    private ShapeRenderer debugShapeRenderer;
    private CancerCell player;
    private Texture bgTexture;
    private Texture wallTexture;

    private boolean ended;
    private boolean debugHitboxesVisible;
    private int score;
    private int infectedCells;
    private int totalTargetCells;
    private float spreadPerCell;
    private float elapsedSeconds;

    public GameScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null || ioController == null) {
            throw new IllegalArgumentException("SceneManager and CellIOController cannot be null.");
        }
        this.sceneManager = sceneManager;
        this.ioController = ioController;
        this.waveManager = new WaveManager();
        this.cancerManager = new CancerEvolutionManager();
        this.chemoManager = new ChemoManager();
        this.interactionSystem = new GameInteractionSystem();
    }

    @Override
    protected void onInitialise() {
        hudRenderer = new GameHudRenderer();
        debugShapeRenderer = new ShapeRenderer();
        bgTexture = getServices().getAssets().getTexture("bg.png");
        wallTexture = getServices().getAssets().getTexture("wall_tile.png");

        score = 0;
        infectedCells = 0;
        elapsedSeconds = 0f;
        ended = false;
        totalTargetCells = 40 * MAX_NORMAL_WAVES;
        spreadPerCell = 100f / totalTargetCells;

        OutputManager output = getServices().getOutputManager();
        output.setCameraBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);

        player = CellFactory.createCancerCell(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f, ioController.getInputMapper());
        createEntity(player);

        spawnNormalCellWave();
        for (int i = 0; i < 2; i++) {
            spawnTCell(randomWalkableX(), randomWalkableY());
        }

        ioController.getAudioHandler().setOrganBGM("lungs");
    }

    @Override
    protected void onUpdate(float delta) {
        if (ioController.getInputMapper().checkDebugHitboxToggle()) {
            debugHitboxesVisible = !debugHitboxesVisible;
        }
        if (ioController.getInputMapper().checkPauseAction()) {
            sceneManager.setActive("pause");
            return;
        }
        if (ended) {
            return;
        }

        elapsedSeconds += delta;
        score = infectedCells * 100 + (int) (elapsedSeconds * 10f);

        clampToArena(player);

        processGameplayInteractions();
        activateChemoIfNeeded();

        float chemoReduction = chemoManager.update(delta);
        if (chemoReduction > 0f) {
            cancerManager.subtractSpread(chemoReduction);
        }

        applyTCellSeparation(delta);
        clampNpcEntities();
        removeInactiveEntities();
        updateSpawning(delta);

        if (cancerManager.checkEvolution() && cancerManager.getCurrentStage() >= 3) {
            ioController.getAudioHandler().playRadioactiveAlert();
        }

        if (player.getHp() <= 0f || elapsedSeconds >= WIN_TIME_SECONDS) {
            loseRun();
            return;
        }

        if (cancerManager.isBodyFullyInfected()) {
            winRun();
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.updateCamera(
                player.getInterpolatedPositionX(interpolationAlpha) + player.getWidth() * 0.5f,
                player.getInterpolatedPositionY(interpolationAlpha) + player.getHeight() * 0.5f);
        output.beginFrame();

        output.beginWorld();
        output.getBatch().draw(bgTexture, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT);
        drawArenaFrame(output);
        output.endWorld();

        output.beginWorld();
        for (Entity entity : getEntities()) {
            if (entity.isActive()) {
                output.drawEntity(entity, interpolationAlpha);
            }
        }
        output.endWorld();

        if (debugHitboxesVisible) {
            renderDebugHitboxes(output, interpolationAlpha);
        }

        hudRenderer.renderWorldHealthBars(output, getEntities(), interpolationAlpha);
        output.beginUi();
        output.endUi();
        hudRenderer.renderUi(
                output,
                player,
                cancerManager,
                chemoManager,
                infectedCells,
                totalTargetCells,
                elapsedSeconds,
                WIN_TIME_SECONDS,
                buildProgressPrompt());
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (hudRenderer != null) {
            hudRenderer.dispose();
        }
        if (debugShapeRenderer != null) {
            debugShapeRenderer.dispose();
        }
    }

    private void processGameplayInteractions() {
        Collection<Entity> nearby = getEntitiesInBounds(buildInteractionQueryArea());

        GameInteractionSystem.InteractionResult result = interactionSystem.process(
                player,
                nearby,
                cancerManager,
                ioController.getAudioHandler(),
                spreadPerCell);

        infectedCells += result.getInfectedCount();
        for (UUID entityId : result.getRemovedEntityIds()) {
            removeEntity(entityId);
        }
        for (int i = 0; i < result.getReplacementTCellCount(); i++) {
            spawnTCell(randomWalkableX(), randomWalkableY());
        }
        if (result.isPlayerKilled()) {
            loseRun();
        }
    }

    private void activateChemoIfNeeded() {
        if (!chemoManager.isActive()
                && cancerManager.getCurrentSpreadPercent() >= CHEMO_ACTIVATION_SPREAD) {
            chemoManager.activate();
        }
    }

    private Rectangle buildInteractionQueryArea() {
        Rectangle playerBounds = player.getBounds();
        return new Rectangle(
                playerBounds.x - INTERACTION_QUERY_PADDING,
                playerBounds.y - INTERACTION_QUERY_PADDING,
                playerBounds.width + INTERACTION_QUERY_PADDING * 2f,
                playerBounds.height + INTERACTION_QUERY_PADDING * 2f);
    }

    private void updateSpawning(float delta) {
        if (waveManager.shouldSpawnTCell(delta, cancerManager.getCurrentStage()) && countTCells() < MAX_ACTIVE_TCELLS) {
            spawnTCell(randomWalkableX(), randomWalkableY());
        }
        if (waveManager.getNormalCellWave() < MAX_NORMAL_WAVES
                && waveManager.shouldSpawnNextNormalCellWave(countNormalCells())) {
            spawnNormalCellWave();
        }
    }

    private void spawnNormalCellWave() {
        int wave = waveManager.getNormalCellWave();
        float speed = Math.min(70f + (wave * 18f), 140f);
        float fleeRange = 120f + (wave * 20f);

        for (int i = 0; i < 40; i++) {
            NormalCell cell = CellFactory.createNormalCell(0f, 0f, speed, fleeRange);
            placeEntityAtSpawn(cell, NORMAL_CELL_MIN_PLAYER_DISTANCE);
            cell.setThreat(player);
            createEntity(cell);
        }
    }

    private void spawnTCell(float x, float y) {
        TCell tCell = CellFactory.createTCell(x, y, 120f + cancerManager.getCurrentStage() * 18f);
        placeEntityAtSpawn(tCell, TCELL_MIN_PLAYER_DISTANCE);
        tCell.setTarget(player);
        createEntity(tCell);
    }

    private void clampNpcEntities() {
        for (Entity entity : getEntities()) {
            if (entity != player && entity.isActive()) {
                clampToArena(entity);
            }
        }
    }

    private void applyTCellSeparation(float delta) {
        for (Entity entity : getEntities()) {
            if (!(entity instanceof TCell) || !entity.isActive()) {
                continue;
            }

            TCell tCell = (TCell) entity;
            Vector2 center = new Vector2(
                    tCell.getPositionX() + tCell.getWidth() * 0.5f,
                    tCell.getPositionY() + tCell.getHeight() * 0.5f);
            Vector2 separation = new Vector2();

            for (Entity nearby : getNearbyEntities(center.x, center.y, TCELL_SEPARATION_RADIUS)) {
                if (!(nearby instanceof TCell) || nearby == tCell || !nearby.isActive()) {
                    continue;
                }

                float otherCenterX = nearby.getPositionX() + nearby.getWidth() * 0.5f;
                float otherCenterY = nearby.getPositionY() + nearby.getHeight() * 0.5f;
                float dx = center.x - otherCenterX;
                float dy = center.y - otherCenterY;
                float distSq = dx * dx + dy * dy;
                if (distSq <= 0.0001f) {
                    separation.add((float) (Math.random() - 0.5f), (float) (Math.random() - 0.5f));
                    continue;
                }

                float distance = (float) Math.sqrt(distSq);
                if (distance >= TCELL_SEPARATION_RADIUS) {
                    continue;
                }

                float weight = (TCELL_SEPARATION_RADIUS - distance) / TCELL_SEPARATION_RADIUS;
                separation.add(dx / distance * weight, dy / distance * weight);
            }

            if (separation.len2() > 0f) {
                separation.nor().scl(TCELL_SEPARATION_STRENGTH * delta);
                tCell.setPosition(
                        tCell.getPositionX() + separation.x,
                        tCell.getPositionY() + separation.y);
            }
        }
    }

    private void clampToArena(Entity entity) {
        float x = Math.max(WORLD_MARGIN, Math.min(entity.getPositionX(), WORLD_WIDTH - WORLD_MARGIN - entity.getWidth()));
        float y = Math.max(WORLD_MARGIN, Math.min(entity.getPositionY(), WORLD_HEIGHT - WORLD_MARGIN - entity.getHeight()));
        entity.setPosition(x, y);
    }

    private void removeInactiveEntities() {
        List<UUID> toRemove = new ArrayList<>();
        for (Entity entity : getEntities()) {
            if (entity == player) {
                continue;
            }
            if (!entity.isActive()) {
                toRemove.add(entity.getId());
            } else if (entity instanceof GameEntity && !((GameEntity) entity).isAlive()) {
                toRemove.add(entity.getId());
            }
        }
        for (UUID id : toRemove) {
            removeEntity(id);
        }
    }

    private int countNormalCells() {
        return countEntitiesOfType(NormalCell.class);
    }

    private int countTCells() {
        return countEntitiesOfType(TCell.class);
    }

    private int countEntitiesOfType(Class<? extends Entity> type) {
        int count = 0;
        for (Entity entity : getEntities()) {
            if (type.isInstance(entity) && entity.isActive()) {
                count++;
            }
        }
        return count;
    }

    private float randomWalkableX() {
        return WORLD_MARGIN + (float) (Math.random() * (WORLD_WIDTH - (WORLD_MARGIN * 2f)));
    }

    private float randomWalkableY() {
        return WORLD_MARGIN + (float) (Math.random() * (WORLD_HEIGHT - (WORLD_MARGIN * 2f)));
    }

    private void placeEntityAtSpawn(GameEntity entity, float minDistanceFromPlayer) {
        Vector2 spawn = findSpawnPosition(entity, minDistanceFromPlayer);
        entity.setPosition(spawn.x, spawn.y);
    }

    private Vector2 findSpawnPosition(GameEntity entity, float minDistanceFromPlayer) {
        float maxX = WORLD_WIDTH - WORLD_MARGIN - entity.getWidth();
        float maxY = WORLD_HEIGHT - WORLD_MARGIN - entity.getHeight();
        float minX = WORLD_MARGIN;
        float minY = WORLD_MARGIN;

        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            float x = minX + (float) (Math.random() * Math.max(1f, maxX - minX));
            float y = minY + (float) (Math.random() * Math.max(1f, maxY - minY));
            entity.setPosition(x, y);
            if (isSpawnPositionValid(entity, minDistanceFromPlayer)) {
                return new Vector2(x, y);
            }
        }

        float fallbackX = Math.max(minX, Math.min(entity.getPositionX(), maxX));
        float fallbackY = Math.max(minY, Math.min(entity.getPositionY(), maxY));
        return new Vector2(fallbackX, fallbackY);
    }

    private boolean isSpawnPositionValid(GameEntity entity, float minDistanceFromPlayer) {
        Rectangle bounds = entity.getBounds();
        Rectangle queryArea = new Rectangle(
                bounds.x - SPAWN_PADDING,
                bounds.y - SPAWN_PADDING,
                bounds.width + SPAWN_PADDING * 2f,
                bounds.height + SPAWN_PADDING * 2f);

        if (player != null) {
            Rectangle playerBounds = player.getBounds();
            if (queryArea.overlaps(playerBounds)) {
                return false;
            }
            float dx = (bounds.x + bounds.width * 0.5f) - (playerBounds.x + playerBounds.width * 0.5f);
            float dy = (bounds.y + bounds.height * 0.5f) - (playerBounds.y + playerBounds.height * 0.5f);
            if (dx * dx + dy * dy < minDistanceFromPlayer * minDistanceFromPlayer) {
                return false;
            }
        }

        for (Entity existing : getEntitiesInBounds(queryArea)) {
            if (!(existing instanceof GameEntity) || !existing.isActive()) {
                continue;
            }
            if (((GameEntity) existing).getBounds().overlaps(queryArea)) {
                return false;
            }
        }

        return true;
    }

    private void drawArenaFrame(OutputManager output) {
        float tileSize = 64f;
        for (float x = 0; x < WORLD_WIDTH; x += tileSize) {
            output.getBatch().draw(wallTexture, x, 0f, tileSize, tileSize);
            output.getBatch().draw(wallTexture, x, WORLD_HEIGHT - tileSize, tileSize, tileSize);
        }
        for (float y = tileSize; y < WORLD_HEIGHT - tileSize; y += tileSize) {
            output.getBatch().draw(wallTexture, 0f, y, tileSize, tileSize);
            output.getBatch().draw(wallTexture, WORLD_WIDTH - tileSize, y, tileSize, tileSize);
        }
    }

    private String buildProgressPrompt() {
        return String.format(Locale.US,
                "Spread through the organ. Infect cells, evade T-cells, survive %.0fs. F3 toggles hitboxes.",
                WIN_TIME_SECONDS);
    }

    private void renderDebugHitboxes(OutputManager output, float interpolationAlpha) {
        output.prepareWorldDebug();
        debugShapeRenderer.setProjectionMatrix(output.getWorldProjectionMatrix());
        debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : getEntities()) {
            if (!(entity instanceof GameEntity) || !entity.isActive()) {
                continue;
            }

            GameEntity gameEntity = (GameEntity) entity;
            debugShapeRenderer.setColor(resolveDebugColor(gameEntity));

            CollisionShape shape = gameEntity.getCollisionShape();
            float offsetX = gameEntity.getInterpolatedPositionX(interpolationAlpha) - gameEntity.getPositionX();
            float offsetY = gameEntity.getInterpolatedPositionY(interpolationAlpha) - gameEntity.getPositionY();
            if (shape.getType() == CollisionShape.Type.CIRCLE) {
                CollisionShape.CircleShape circle = (CollisionShape.CircleShape) shape;
                debugShapeRenderer.circle(circle.cx + offsetX, circle.cy + offsetY, circle.radius, 28);
            } else {
                Rectangle bounds = gameEntity.getBounds();
                debugShapeRenderer.rect(bounds.x + offsetX, bounds.y + offsetY, bounds.width, bounds.height);
            }
        }

        debugShapeRenderer.end();
    }

    private Color resolveDebugColor(GameEntity entity) {
        if (entity == player) {
            return Color.CYAN;
        }
        if (entity instanceof TCell) {
            return Color.RED;
        }
        if (entity instanceof NormalCell) {
            return Color.GREEN;
        }
        return Color.WHITE;
    }

    private void winRun() {
        ended = true;
        RunStats.recordRun(score, elapsedSeconds, true);
        sceneManager.setActive("win");
    }

    private void loseRun() {
        ended = true;
        RunStats.recordRun(score, elapsedSeconds, false);
        sceneManager.setActive("lose");
    }
}
