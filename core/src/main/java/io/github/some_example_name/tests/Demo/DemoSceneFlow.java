package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.scene.SceneManager;

public final class DemoSceneFlow {
    private DemoSceneFlow() {}

    public static void restartMainRun(SceneManager sm) {
        sm.unload("main");
        sm.load("main", new MainScene(sm));
        sm.setActive("main");
    }

    public static void goToStart(SceneManager sm) {
        sm.setActive("start");
    }
}
