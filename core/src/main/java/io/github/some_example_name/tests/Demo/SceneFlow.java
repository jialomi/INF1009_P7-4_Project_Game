package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.scene.SceneManager;

public final class SceneFlow {
    private SceneFlow() {}

    public static void restartMainRun(SceneManager sm, EngineServices services) {
        sm.unload("main");
        sm.load("main", new MainScene(sm, services));
        sm.setActive("main");
    }

    public static void goToStart(SceneManager sm) {
        sm.setActive("start");
    }
}
