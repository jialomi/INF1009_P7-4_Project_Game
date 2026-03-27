package io.github.some_example_name.game.scene;

import java.util.Collection;
import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.engine.collision.CollisionShape; 
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.config.GameBalanceConfig;
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
    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private final WaveManager waveManager;
    private final CancerEvolutionManager cancerManager;
    private final ChemoManager chemoManager;
    private final GameInteractionSystem interactionSystem;
    private final GameWorldSystem worldSystem;

    private GameHudRenderer hudRenderer;
    private ShapeRenderer debugShapeRenderer;
    private CancerCell player;
    private Texture bgTexture;
    private Texture wallTexture;

    private boolean ended;
    private boolean debugHitboxesVisible;
    private int score;
    private int infectedCells;
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
        this.worldSystem = new GameWorldSystem(this, waveManager, cancerManager);
    }

    @Override
    protected void onInitialise() {
        // pass asset manager into hud so it can grab key textures
        hudRenderer = new GameHudRenderer(getServices().getAssets());
        debugShapeRenderer = new ShapeRenderer();
        bgTexture = getServices().getAssets().getTexture("images/bg.png");
        wallTexture = getServices().getAssets().getTexture("images/wall_tile.png");

        score = 0;
        infectedCells = 0;
        elapsedSeconds = 0f;
        ended = false;

        OutputManager output = getServices().getOutputManager();
        output.setCameraBounds(0f, 0f, GameBalanceConfig.WORLD_WIDTH, GameBalanceConfig.WORLD_HEIGHT);

        player = CellFactory.createCancerCell(
                GameBalanceConfig.WORLD_WIDTH * 0.5f,
                GameBalanceConfig.WORLD_HEIGHT * 0.5f,
                ioController.getInputMapper());
        createEntity(player);
        worldSystem.setPlayer(player);

        worldSystem.spawnInitialEntities();

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
        if (player.getHp() <= 0f) {
            loseRun();
            return;
        }

        elapsedSeconds += delta;
        score = infectedCells * 100 + (int) (elapsedSeconds * 10f);

        worldSystem.clampToArena(player);

        processGameplayInteractions();
        if (ended) {
            return;
        }
        activateChemoIfNeeded();

        float chemoReduction = chemoManager.update(
                delta,
                cancerManager.getCurrentStage(),
                cancerManager.getCurrentSpreadPercent());
        if (chemoReduction > 0f) {
            cancerManager.subtractSpread(chemoReduction);
        }

        worldSystem.updateTCellAggression();
        worldSystem.applyTCellSeparation(delta);
        worldSystem.clampNpcEntities();
        worldSystem.recycleIneffectiveTCells();
        worldSystem.removeInactiveEntities();
        worldSystem.updateSpawning(delta);

        if (cancerManager.checkEvolution() && cancerManager.getCurrentStage() >= 3) {
            ioController.getAudioHandler().playRadioactiveAlert();
        }

        if (player.getHp() <= 0f) {
            loseRun();
            return;
        }

        if (cancerManager.isBodyFullyInfected()) {
            winRun();
            return;
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
        output.getBatch().draw(bgTexture, 0f, 0f, GameBalanceConfig.WORLD_WIDTH, GameBalanceConfig.WORLD_HEIGHT);
        worldSystem.drawArenaFrame(output, wallTexture);
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
                elapsedSeconds,
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
                GameBalanceConfig.SPREAD_PER_HEALTHY_CELL);

        infectedCells += result.getInfectedCount();
        for (UUID entityId : result.getRemovedEntityIds()) {
            removeEntity(entityId);
        }
        worldSystem.spawnReplacementTCells(result.getReplacementTCellCount());
        if (result.isPlayerKilled()) {
            loseRun();
            return;
        }
    }

    private void activateChemoIfNeeded() {
        if (!chemoManager.isActive()
                && cancerManager.getCurrentSpreadPercent() >= GameBalanceConfig.CHEMO_ACTIVATION_SPREAD) {
            chemoManager.activate();
        }
    }

    private Rectangle buildInteractionQueryArea() {
        Rectangle playerBounds = player.getBounds();
        return new Rectangle(
                playerBounds.x - GameBalanceConfig.INTERACTION_QUERY_PADDING,
                playerBounds.y - GameBalanceConfig.INTERACTION_QUERY_PADDING,
                playerBounds.width + GameBalanceConfig.INTERACTION_QUERY_PADDING * 2f,
                playerBounds.height + GameBalanceConfig.INTERACTION_QUERY_PADDING * 2f);
    }

    private String buildProgressPrompt() {
        return "Infect healthy cells to raise spread. Chemo starts at 40%. Terminal stage starts at 90%.";
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
        RunStats.recordRun(score, infectedCells, player.getLevel(), cancerManager.getCurrentSpreadPercent(),
                elapsedSeconds, true);
        sceneManager.setActive("win");
    }

    private void loseRun() {
        ended = true;
        RunStats.recordRun(score, infectedCells, player.getLevel(), cancerManager.getCurrentSpreadPercent(),
                elapsedSeconds, false);
        sceneManager.setActive("lose");
    }
}
