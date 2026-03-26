package io.github.some_example_name.game.util;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.scene.GameScene;

public final class SceneFlow {
    private SceneFlow() {}

    public static void restartGame(SceneManager sm, EngineServices services, CellIOController ioController) {
        sm.unload("game");
        sm.load("game", new GameScene(sm, services, ioController));
        sm.setActive("game");
    }

    public static void goToStart(SceneManager sm) {
        sm.setActive("start");
    }
}
