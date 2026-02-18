// core/src/main/java/io/github/some_example_name/tests/Demo/TestMainScene.java
package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.engine.collision.Collidable;
import java.util.Locale;

public class TestMainScene extends AbstractScene {

    private static final int START_LIVES = 99;
    private static final float WIN_TIME_SECONDS = 45f;
    private static final float BASE_ENEMY_SPEED = 150f;
    private static final float MAX_ENEMY_SPEED = 340f;
    private static final int MAX_ENEMIES = 9;
    private static final float INITIAL_SPAWN_INTERVAL = 7f;
    private static final float MIN_SPAWN_INTERVAL = 2.5f;
    private static final float ENEMY_WIDTH = 48f;
    private static final float BORDER_THICKNESS = 8f;
    private final SceneManager sceneManager;


    private TestPlayer player;
    private BitmapFont font;

    private int lives;
    private int score;
    private float elapsed;
    private float spawnTimer;
    private float spawnInterval;
    private boolean ended;
    private int wave;
    private float waveTimer;
    private static final float WAVE_INTERVAL = 12f;

    public TestMainScene(SceneManager sceneManager) {
        if (sceneManager == null) {
            throw new IllegalArgumentException("SceneManager cannot be null");
        }
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        lives = START_LIVES;
        score = 0;
        elapsed = 0f;
        spawnTimer = 0f;
        spawnInterval = INITIAL_SPAWN_INTERVAL;
        ended = false;
        wave = 1;
        waveTimer = 0f;

        createEntity(new TestWall(200, 300, 64));
        createEntity(new TestWall(550, 400, 64));

        player = new TestPlayer("Hero", 400, 100);
        OutputManager output = IOManager.getInstance().getOutputManager();
        createSideBoundaryWalls(output);
        // Keep player within side walls
        player.setMovementBounds(
            BORDER_THICKNESS,
            0f,
            output.getWorldWidth() - BORDER_THICKNESS,
            output.getWorldHeight()
        );

        createEntity(player);

        spawnEnemySafely("Drop 1", 600f);
        spawnEnemySafely("Drop 2", 750f);
        spawnEnemySafely("Drop 3", 900f);
    }

    private void createSideBoundaryWalls(OutputManager output) {
        float worldW = output.getWorldWidth();
        float worldH = output.getWorldHeight();

        createEntity(new TestBoundaryWall(0f, 0f, BORDER_THICKNESS, worldH));
        createEntity(new TestBoundaryWall(worldW - BORDER_THICKNESS, 0f, BORDER_THICKNESS, worldH));
    }

    @Override
    protected void onUpdate(float delta) {
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.P)) {
            sceneManager.setActive("pause");
            return;
        }

        if (ended) return;

        elapsed += delta;
        waveTimer += delta;
        if (waveTimer >= WAVE_INTERVAL) {
            waveTimer = 0f;
            wave++;
            spawnInterval = Math.max(MIN_SPAWN_INTERVAL, spawnInterval - 0.35f);
        }
        score = (int) (elapsed * 100f);

        updateEnemyDifficulty();
        updateSpawning(delta);

        if (player.consumeHitEvent()) {
            lives--;
            if (lives <= 0) {
                ended = true;
                DemoRunStats.recordRun(score, elapsed, false);
                sceneManager.setActive("lose");
                return;
            }
        }

        if (elapsed >= WIN_TIME_SECONDS) {
            ended = true;
            DemoRunStats.recordRun(score, elapsed, true);
            sceneManager.setActive("win");
            return;
        }

        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.SPACE)) {
            IOManager.getInstance().getAudio().playSound("test.mp3");
        }
    }

    private void updateEnemyDifficulty() {
        float scaledSpeed = Math.min(BASE_ENEMY_SPEED + elapsed * 3f + (wave - 1) * 25f, MAX_ENEMY_SPEED);
        for (Entity e : getEntities()) {
            if (e instanceof TestEnemy) {
                ((TestEnemy) e).setSpeed(scaledSpeed);
            }
        }
    }

    private void updateSpawning(float delta) {
        spawnTimer += delta;
        if (spawnTimer < spawnInterval) return;

        spawnTimer = 0f;
        spawnInterval = Math.max(MIN_SPAWN_INTERVAL, spawnInterval - 0.2f);

        if (countEnemies() < MAX_ENEMIES) {
            spawnEnemySafely("Wave", 680f);
        }
    }

    private float randomEnemySpawnX(OutputManager output) {
        float minX = BORDER_THICKNESS + 2f;
        float maxX = output.getWorldWidth() - BORDER_THICKNESS - ENEMY_WIDTH - 2f;
        return minX + (float) (Math.random() * Math.max(1f, (maxX - minX)));
    }

    private boolean overlapsWallAt(float x, float y, float w, float h) {
        Rectangle candidate = new Rectangle(x, y, w, h);
        for (Entity e : getEntities()) {
            if ((e instanceof TestWall || e instanceof TestBoundaryWall) && e instanceof Collidable) {
                Rectangle b = ((Collidable) e).getBounds();
                if (b != null && candidate.overlaps(b)) return true;
            }
        }
        return false;
    }

    private void spawnEnemySafely(String name, float y) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        final int MAX_ATTEMPTS = 10;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            float x = randomEnemySpawnX(output);
            if (!overlapsWallAt(x, y, ENEMY_WIDTH, ENEMY_WIDTH)) {
                createEntity(new TestEnemy(name, x, y));
                return;
            }
        }

        float fallbackX = (output.getWorldWidth() - ENEMY_WIDTH) * 0.5f;
        createEntity(new TestEnemy(name, fallbackX, y));
    }


    private int countEnemies() {
        int count = 0;
        for (Entity e : getEntities()) {
            if (e instanceof TestEnemy) count++;
        }
        return count;
    }

    @Override
    public void render(float delta) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        output.beginFrame();

        for (Entity entity : getEntities()) {
            output.drawEntity(entity);
        }

        float top = output.getWorldHeight() - 15f;
        font.draw(output.getBatch(), "LIVES: " + lives, 20, top);
        font.draw(output.getBatch(), "SCORE: " + score, 20, top - 24f);
        font.draw(output.getBatch(),
            "TIME: " + String.format(Locale.US, "%.1f / %.0fs", elapsed, WIN_TIME_SECONDS), 20, top - 48f);
        font.draw(output.getBatch(), "P:PAUSE  SPACE:SFX", 20, top - 72f);

        if (player != null && player.isInvulnerable()) {
            font.draw(output.getBatch(), "HIT COOLING...", 20, top - 96f);
        }

        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();

        for (Entity e : getEntities()) {
            if (e instanceof TestWall) ((TestWall) e).dispose();
            if (e instanceof TestBoundaryWall) ((TestBoundaryWall) e).dispose();
            if (e instanceof TestPlayer) ((TestPlayer) e).dispose();
            if (e instanceof TestEnemy) ((TestEnemy) e).dispose();
        }
    }
}
