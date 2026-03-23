package io.github.some_example_name.game.scene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.CellFactory;
import io.github.some_example_name.game.entity.HealthBar;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.ChemoManager;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.WaveManager;

public class GameScene extends AbstractScene {

    // -------------------------------------------------------------------------
    // ORIGINAL TIGHT LUNG MAP (9x5 Grid)
    // -------------------------------------------------------------------------
    private static final int[][] LUNGS_MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 2, 0, 0, 0, 0, 1}, // Player Spawn
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    private static final int MAP_COLS = 9;
    private static final int MAP_ROWS = 5;
    private float tileW, tileH;

    // -------------------------------------------------------------------------
    // DEATH PARTICLE — fading circle at dead cell position
    // -------------------------------------------------------------------------
    private static class DeathParticle {
        float x, y, radius, timer, maxTimer;
        Color color;
        DeathParticle(float x, float y, float radius, Color color, float duration) {
            this.x = x; this.y = y; this.radius = radius;
            this.color = color.cpy();
            this.maxTimer = duration; this.timer = duration;
        }
        boolean update(float delta) { timer -= delta; return timer > 0f; }
        float alpha() { return Math.max(0f, timer / maxTimer); }
    }

    // -------------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------------
    private static final float WIN_TIME_SECONDS    = 120f;
    private static final int   MAX_TCELLS          = 25;
    private static final float GROWTH_PER_CELL     = 15f;  // very visible growth
    private static final float SPREAD_PER_CELL     = 3f;
    private static final float TCELL_DAMAGE        = 15f;
    private static final float DAMAGE_COOLDOWN_SEC = 1.0f;

    private final SceneManager sceneManager;
    private CancerCell player;
    private CellIOController ioController;

    private BitmapFont    font;
    private ShapeRenderer shapeRenderer;
    private Texture       bgTexture;
    private Texture       wallTexture;

    private final List<DeathParticle> particles  = new ArrayList<>();
    private final Set<Entity>         eatenCells = new HashSet<>();

    private int     score;
    private float   elapsed;
    private float   tCellDamageCooldown = 0f;
    private boolean ended;

    private WaveManager            waveManager;
    private CancerEvolutionManager cancerManager;
    private ChemoManager           chemoManager;

    public GameScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null)
            throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager  = sceneManager;
        this.waveManager   = new WaveManager();
        this.cancerManager = new CancerEvolutionManager();
        this.chemoManager  = new ChemoManager();
        this.ioController = new CellIOController(services);
    }

    // -------------------------------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------------------------------

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        shapeRenderer = new ShapeRenderer();

        bgTexture   = new Texture("bg.png");
        wallTexture = new Texture("wall_tile.png");

        OutputManager output = getServices().getOutputManager();
        
        // Set camera bounds to the massive 2000x2000 world!
        output.setCameraBounds(0f, 0f, 2000f, 2000f);

        score               = 0;
        elapsed             = 0f;
        tCellDamageCooldown = 0f;
        ended               = false;
        particles.clear();
        eatenCells.clear();

        chemoManager.activate();

        // Spawn player dead center in the massive world
        player = CellFactory.createCancerCell(getServices().getInput(), 1000f, 1000f);
        createEntity(player);

        spawnTCell(1000f);
        spawnNormalCellWave();
        ioController.getAudioHandler().setOrganBGM("Lungs");
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    protected void onUpdate(float delta) {
        if (getServices().getInput().isKeyJustPressed(Input.Keys.P)) {
            sceneManager.setActive("pause");
            return;
        }
        if (ended) return;

        // Force the camera to smoothly track the center of the player
        OutputManager output = getServices().getOutputManager();
        output.updateCamera(player.getPositionX() + (player.getWidth() / 2f), player.getPositionY() + (player.getHeight() / 2f));
        // ---------------------------------//

        elapsed += delta;
        score    = (int)(elapsed * 100f);

        // Cooldown tick
        if (tCellDamageCooldown > 0f) tCellDamageCooldown -= delta;

        // Update death particles
        Iterator<DeathParticle> it = particles.iterator();
        while (it.hasNext()) {
            if (!it.next().update(delta)) it.remove();
        }

        // Chemo — reduces spread on timer
        float reduction = chemoManager.update(delta);
        if (reduction > 0f) cancerManager.subtractSpread(reduction);

       // -----------------------------------------------------------------------
        // COLLISION — The "Physics-Bypass" Check
        // -----------------------------------------------------------------------
        for (Entity entity : getEntities()) {
            if (entity == player || !entity.isActive() || eatenCells.contains(entity)) continue;

            float px = player.getPositionX() + (player.getWidth() / 2f);
            float py = player.getPositionY() + (player.getHeight() / 2f);
            float ex = entity.getPositionX() + (entity.getWidth() / 2f);
            float ey = entity.getPositionY() + (entity.getHeight() / 2f);

            float distance = (float) Math.hypot(px - ex, py - ey);
            float touchDistance = (player.getWidth() / 2f) + (entity.getWidth() / 2f);

            // Add a generous 15-pixel buffer. The moment they bump into your physics wall,
            // we catch it and eat them before they visibly bounce away!
            if (distance <= touchDistance + 15f) {
                
                if (entity instanceof NormalCell) {
                    eatenCells.add(entity);
                    entity.setActive(false);
                    entity.setPosition(-1000f, -1000f); // Vanish instantly

                    ioController.getAudioHandler().playEatCellSquelch();
                    
                    // Use the built-in leveling system! Fixes the shrinking HP bar!
                    player.gainExp(50f); 
                    cancerManager.addSpread(SPREAD_PER_CELL);

                } else if (entity instanceof TCell) {
                    TCell tcell = (TCell) entity;

                    // Stage 4: Eat T-Cells
                    if (cancerManager.canEatTCells()) {
                        tcell.takeDamage(5f); 
                        if (!tcell.isAlive()) {
                            eatenCells.add(entity);
                            entity.setActive(false);
                            entity.setPosition(-1000f, -1000f);

                            ioController.getAudioHandler().playEatCellSquelch();

                            player.gainExp(100f); // T-Cells give huge EXP
                            cancerManager.addSpread(5f);
                        }
                    } 
                    
                    // Take damage from T-Cells
                    if (tCellDamageCooldown <= 0f && !cancerManager.canEatTCells()) {
                        ioController.getAudioHandler().playTCellDamage();
                        player.takeDamage(TCELL_DAMAGE);
                        tCellDamageCooldown = DAMAGE_COOLDOWN_SEC;
                        
                        // You died before infecting the body. YOU LOSE.
                        if (player.getHp() <= 0) {
                            ended = true;
                            RunStats.recordRun(score, elapsed, false);
                            sceneManager.setActive("lose"); // Changed back to lose!
                        }
                    }
                }
            }
        } // end collision for loop


        // T-cell spawning
        if (waveManager.shouldSpawnTCell(delta, cancerManager.getCurrentStage())
                && countTCells() < MAX_TCELLS) {
            spawnTCell(randomWalkableX());
        }

        // Normal cell wave — next batch when all eaten
        if (waveManager.shouldSpawnNextNormalCellWave(countNormalCells())) {
            spawnNormalCellWave();
        }

        // Evolution
        if (cancerManager.checkEvolution()) {
            int stage = cancerManager.getCurrentStage();
            System.out.println("EVOLUTION: Stage " + stage + "!");
            if (stage >= 3) cancerManager.triggerRadioactiveWave();
            if (waveManager.isNewStageThreshold(stage)) {
                for (int i = 0; i < stage && countTCells() < MAX_TCELLS; i++) {
                    spawnTCell(randomWalkableX());
                }
            }
        }

        // -----------------------------------------------------------------------
        // WIN / LOSE CONDITIONS (Correct Player Perspective)
        // -----------------------------------------------------------------------
        // You successfully hit 100% spread. YOU WIN!
        if (cancerManager.isBodyFullyInfected()) {
            ended = true;
            RunStats.recordRun(score, elapsed, true);
            sceneManager.setActive("win"); // Changed to win!
        }
        // Time ran out before you could spread. YOU LOSE!
        if (elapsed >= WIN_TIME_SECONDS) {
            ended = true;
            RunStats.recordRun(score, elapsed, false);
            sceneManager.setActive("lose"); // Changed to lose!
        }
    }

    // -------------------------------------------------------------------------
    // GROWTH — sole authority, uses applySize which now preserves HP ratio
    // -------------------------------------------------------------------------

    private void growPlayer(float amount) {
        player.applySize(player.getSize() + amount);
    }

    // -------------------------------------------------------------------------
    // SPAWNING — always via CellFactory
    // -------------------------------------------------------------------------

    private void spawnNormalCellWave() {
        int amountToSpawn = 40; // Spawns a massive feast of 40 cells!
        System.out.println("[Wave " + waveManager.getNormalCellWave()
            + "] Spawning " + amountToSpawn + " normal cells.");
            
        int spawned = 0, attempts = 0;
        // Increased attempts to 300 to ensure they all find a spot in the large map
        while (spawned < amountToSpawn && attempts < 300) { 
            attempts++;
            float x = randomWalkableX();
            float y = randomWalkableY();
            if (isWalkable(x, y)) {
                NormalCell cell = CellFactory.createNormalCell(x, y);
                createEntity(cell);
                cell.setThreat(player);
                spawned++;
            }
        }
    }

    private void spawnTCell(float x) {
        float y = 1900f; // Spawn near the top of the massive map
        TCell tcell = CellFactory.createTCell(x, y);
        createEntity(tcell);
        tcell.setTarget(player);
    }

    // -------------------------------------------------------------------------
    // MAP HELPERS
    // -------------------------------------------------------------------------

    private boolean isWalkable(float worldX, float worldY) {
        return worldX > 64f && worldX < 1936f && worldY > 64f && worldY < 1936f;
    }

    private float randomWalkableX() {
        return 64f + (float)(Math.random() * (2000f - 128f));
    }

    private float randomWalkableY() {
        return 64f + (float)(Math.random() * (2000f - 128f));
    }

    private int countTCells() {
        int n = 0;
        for (Entity e : getEntities())
            if (e instanceof TCell && e.isActive()) n++;
        return n;
    }

    private int countNormalCells() {
        int n = 0;
        for (Entity e : getEntities())
            if (e instanceof NormalCell && e.isActive()) n++;
        return n;
    }

    // -------------------------------------------------------------------------
    // RENDER
    // -------------------------------------------------------------------------

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();

        // -------------------------------------------------------------------------
        // LAYER 2: THE WORLD (Slides around with the Camera)
        // -------------------------------------------------------------------------
        output.beginWorld();
        // Draw the massive background
        output.getBatch().draw(bgTexture, 0, 0, 2000f, 2000f);
        output.endWorld();

        output.beginWorld();
        for (Entity entity : getEntities()) {
            if (!entity.isActive()) continue;
            if (entity == player) {
                TextureRegion tex = player.getCurrentTexture();
                if (tex != null) {
                    output.getBatch().draw(tex, player.getPositionX(), player.getPositionY(), player.getSize(), player.getSize());
                }
            } else {
                output.drawEntity(entity, interpolationAlpha);
            }
        }
        output.endWorld();

        shapeRenderer.setProjectionMatrix(output.getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Entity entity : getEntities()) {
            if (!entity.isActive()) continue;
            HealthBar hb = null;
            if      (entity instanceof CancerCell) hb = ((CancerCell) entity).getHealthBar();
            else if (entity instanceof NormalCell) hb = ((NormalCell) entity).getHealthBar();
            else if (entity instanceof TCell)      hb = ((TCell)      entity).getHealthBar();
            if (hb != null) hb.render(shapeRenderer);
        }
        shapeRenderer.end();

        // -------------------------------------------------------------------------
        // LAYER 1: UI + THE PICTURE FRAME (Glued to the Screen)
        // -------------------------------------------------------------------------
        output.beginUi();
        
        // 1. Draw the visual Wall Frame around the screen edge!
        float uiW = output.getUiWidth();
        float uiH = output.getUiHeight();
        float wallSize = 64f; 

        for (float x = 0; x < uiW; x += wallSize) {
            output.getBatch().draw(wallTexture, x, 0, wallSize, wallSize); // Bottom
            output.getBatch().draw(wallTexture, x, uiH - wallSize, wallSize, wallSize); // Top
        }
        for (float y = wallSize; y < uiH - wallSize; y += wallSize) {
            output.getBatch().draw(wallTexture, 0, y, wallSize, wallSize); // Left
            output.getBatch().draw(wallTexture, uiW - wallSize, y, wallSize, wallSize); // Right
        }

        shapeRenderer.setProjectionMatrix(output.getBatch().getProjectionMatrix());
        output.endUi();

        // 2. Draw Dark UI Panels
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 0.85f);
        shapeRenderer.rect(10, output.getUiHeight() - 115, 680, 105);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        shapeRenderer.rect(10, 10, output.getUiWidth() - 20, 30);

        float spread = cancerManager.getCurrentSpreadPercent();
        float barWidth = (output.getUiWidth() - 20) * (spread / 100f);
        shapeRenderer.setColor(0.7f, 0.1f, 0.3f, 1f); 
        shapeRenderer.rect(10, 10, barWidth, 30);

        shapeRenderer.end();
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        // 3. Draw UI Text
        output.beginUi();
        float top      = output.getUiHeight() - 25f;
        float leftCol  = 20f;
        float rightCol = 240f; 

        font.setColor(Color.WHITE);
        font.draw(output.getBatch(), "HP: " + (int) player.getHp() + " / " + (int) player.getMaxHp(), leftCol, top);
        font.draw(output.getBatch(), "SCORE: " + score,  leftCol, top - 24f);
        font.draw(output.getBatch(), String.format(Locale.US, "TIME: %.1f / %.0fs", elapsed, WIN_TIME_SECONDS), leftCol, top - 48f);
        font.draw(output.getBatch(), "[P] PAUSE", leftCol, top - 72f);

        font.setColor(new Color(0.8f, 0.3f, 1f, 1f));
        font.draw(output.getBatch(), "SPREAD: " + (int) spread + "%", rightCol, top);

        font.setColor(new Color(1f, 0.4f, 0.4f, 1f));
        font.draw(output.getBatch(), cancerManager.getImmuneStrengthDescription(), rightCol, top - 24f);

        if (chemoManager.isActive()) {
            float t = chemoManager.getTimeUntilNextChemo();
            font.setColor(t <= 5f ? Color.RED : Color.ORANGE);
            font.draw(output.getBatch(), t <= 5f ? "!! CHEMO IN " + (int) t + "s !!" : "Next chemo: " + (int) t + "s", rightCol, top - 48f);
        }

        font.setColor(Color.YELLOW);
        String stageDesc = "";
        switch (cancerManager.getCurrentStage()) {
            case 1:  stageDesc = "STAGE 1: Undetected - eat and grow!";  break;
            case 2:  stageDesc = "STAGE 2: Detected - chemo active!";     break;
            case 3:  stageDesc = "STAGE 3: Aggressive - waves incoming!"; break;
            case 4:  stageDesc = "STAGE 4: Dominant - eat T-Cells!";      break;
        }
        font.draw(output.getBatch(), stageDesc, rightCol, top - 72f);

        font.setColor(Color.WHITE);
        font.draw(output.getBatch(), "INFECTION SPREAD: " + (int) spread + "%", leftCol, 32f);

        output.endUi();
        output.endFrame();
    }

    // -------------------------------------------------------------------------
    // DISPOSE
    // -------------------------------------------------------------------------

    @Override
    protected void onDispose() {
        if (font          != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (bgTexture     != null) bgTexture.dispose();
        if (wallTexture   != null) wallTexture.dispose();
    }
}