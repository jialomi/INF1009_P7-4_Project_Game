package io.github.some_example_name.game.scene;

import java.util.Locale;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.util.RunStats;

public class GameScene extends AbstractScene {

    private static final int START_LIVES = 5;
    private static final float WIN_TIME_SECONDS = 45f;
    private static final int MAX_TCELLS = 9;
    private static final float INITIAL_SPAWN_INTERVAL = 7f;
    private static final float MIN_SPAWN_INTERVAL = 2.5f;
    private static final float WAVE_INTERVAL = 12f;

    private final SceneManager sceneManager;

    private CancerCell player;
    private BitmapFont font;

    private int lives;
    private int score;
    private float elapsed;
    private float spawnTimer;
    private float spawnInterval;
    private boolean ended;
    private int wave;
    private float waveTimer;

    public GameScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null) throw new IllegalArgumentException("SceneManager cannot be null");
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

        player = new CancerCell(getServices().getInput(), 400f, 100f);
        createEntity(player);

        // spawn T-cells
        spawnTCell(300f);
        spawnNormalCell(500f);
    }

    @Override
    protected void onUpdate(float delta) {
        if (getServices().getInput().isKeyJustPressed(Input.Keys.P)) {
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

        updateSpawning(delta);

        if (elapsed >= WIN_TIME_SECONDS) {
            ended = true;
            RunStats.recordRun(score, elapsed, true);
            sceneManager.setActive("win");
        }
    }

    private void updateSpawning(float delta) {
        spawnTimer += delta;
        if (spawnTimer < spawnInterval) return;

        spawnTimer = 0f;
        spawnInterval = Math.max(MIN_SPAWN_INTERVAL, spawnInterval - 0.2f);

        if (countTCells() < MAX_TCELLS) {
            spawnTCell((float) (Math.random() * 700f));
        }
    }

    private void spawnTCell(float x) {
        TCell tcell = new TCell(x, 650f);
        createEntity(tcell);
        tcell.setTarget(player);

    }

    private void spawnNormalCell(float x) {
        NormalCell cell = new NormalCell(x, 650f);
        createEntity(cell);
        cell.setThreat(player);
    }

    private int countTCells() {
        int count = 0;
        for (Entity e : getEntities()) {
            if (e instanceof TCell) count++;
        }
        return count;
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginWorld();

        for (Entity entity : getEntities()) {
            output.drawEntity(entity, interpolationAlpha);
        }
        output.endWorld();

        output.beginUi();
        float top = output.getUiHeight() - 15f;
        font.draw(output.getBatch(), "LIVES: " + lives, 20, top);
        font.draw(output.getBatch(), "SCORE: " + score, 20, top - 24f);
        font.draw(output.getBatch(),
            "TIME: " + String.format(Locale.US, "%.1f / %.0fs", elapsed, WIN_TIME_SECONDS), 20, top - 48f);
        font.draw(output.getBatch(), "P: PAUSE", 20, top - 72f);
        output.endUi();

        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();
    }
}
