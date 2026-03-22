package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputConfiguration;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.tests.Demo.TextureFactory;
import io.github.some_example_name.tests.Demo.LoseScene;
import io.github.some_example_name.tests.Demo.MainScene;
import io.github.some_example_name.tests.Demo.PauseScene;
import io.github.some_example_name.tests.Demo.StartScene;
import io.github.some_example_name.tests.Demo.WinScene;

public class GameMaster extends Game {
    private static final float LOGIC_STEP_SECONDS = 1f / 60f;
    private static final int MAX_LOGIC_STEPS_PER_FRAME = 5;

    private EngineServices services;
    private SceneManager sceneManager;

    @Override
    public void create() {
        System.out.println("==========================================");
        System.out.println("=      GAME ENGINE INITIALISATION        =");
        System.out.println("==========================================");

        services = new EngineServices(new OutputConfiguration(800f, 600f, 0f, 0f, 0f, 1f));
        services.initialize();
        services.getAudio().preloadSound("crash.mp3");
        services.getAudio().preloadSound("test.mp3");

        if (services.getOutputManager() != null) {
            services.getOutputManager().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        sceneManager = new SceneManager(LOGIC_STEP_SECONDS, MAX_LOGIC_STEPS_PER_FRAME);
        sceneManager.setOnSceneActivated(() -> services.getInput().clearJustPressed());

        sceneManager.load("start", new StartScene(sceneManager, services));
        sceneManager.load("main", new MainScene(sceneManager, services));
        sceneManager.load("pause", new PauseScene(sceneManager, services));
        sceneManager.load("win", new WinScene(sceneManager, services));
        sceneManager.load("lose", new LoseScene(sceneManager, services));

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
        if (services != null && services.getOutputManager() != null) {
            services.getOutputManager().resize(width, height);
        }
        if (sceneManager != null) {
            sceneManager.resize(width, height);
        }
    }
    
    @Override
    public void dispose() {
        if (sceneManager != null) sceneManager.dispose();
        if (services != null) services.dispose();
        TextureFactory.disposeAll();
    }
}
