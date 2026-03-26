package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputConfiguration;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.entity.CellFactory;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.GameAssetCatalog;
import io.github.some_example_name.game.scene.GameScene;
import io.github.some_example_name.game.scene.LoseScene;
import io.github.some_example_name.game.scene.PauseScene;
import io.github.some_example_name.game.scene.StartScene;
import io.github.some_example_name.game.scene.WinScene;

public class GameMaster extends Game {
    private static final float LOGIC_STEP_SECONDS = 1f / 60f;
    private static final int MAX_LOGIC_STEPS_PER_FRAME = 5;

    private EngineServices services;
    private SceneManager sceneManager;
    private CellIOController ioController;

    @Override
    public void create() {
        System.out.println("==========================================");
        System.out.println("=      GAME ENGINE INITIALISATION        =");
        System.out.println("==========================================");

        services = new EngineServices(new OutputConfiguration(800f, 600f, 0f, 0f, 0f, 1f));
        services.initialize();
        CellFactory.initializeSharedAssets(services.getAssets());
        GameAssetCatalog.preloadAll(services.getAssets(), services.getAudio());
        ioController = new CellIOController(services);

        services.getOutputManager().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sceneManager = new SceneManager(LOGIC_STEP_SECONDS, MAX_LOGIC_STEPS_PER_FRAME);
        sceneManager.setOnSceneActivated(() -> services.getInput().clearJustPressed());

        sceneManager.load("start", new StartScene(sceneManager, services, ioController));
        sceneManager.load("game", new GameScene(sceneManager, services, ioController));
        sceneManager.load("pause", new PauseScene(sceneManager, services, ioController));
        sceneManager.load("win", new WinScene(sceneManager, services, ioController));
        sceneManager.load("lose", new LoseScene(sceneManager, services, ioController));

        sceneManager.setActive("start");
        System.out.println("Engine Online: Start Scene Loaded");
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        sceneManager.runFrame(delta);
    }

    @Override
    public void resize(int width, int height) {
        if (services != null) {
            services.getOutputManager().resize(width, height);
        }
        if (sceneManager != null) {
            sceneManager.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (sceneManager != null) {
            sceneManager.dispose();
        }
        if (ioController != null) {
            ioController.dispose();
        }
        if (services != null) {
            services.dispose();
        }
    }
}
