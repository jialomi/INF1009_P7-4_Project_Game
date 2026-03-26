package io.github.some_example_name.game.tests;

import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.ChemoManager;
import io.github.some_example_name.game.util.WaveManager;

public final class GameRegressionSelfTest {
    public static void main(String[] args) {
        testWaveManager();
        testCancerEvolution();
        testChemoManager();
        System.out.println("All game regression checks passed.");
    }

    private static void testWaveManager() {
        WaveManager waveManager = new WaveManager();
        require(!waveManager.shouldSpawnNextNormalCellWave(3), "Wave should not advance while cells remain.");
        require(waveManager.shouldSpawnNextNormalCellWave(0), "Wave should advance when no healthy cells remain.");
        require(waveManager.getNormalCellWave() == 2, "Wave count should increment.");
    }

    private static void testCancerEvolution() {
        CancerEvolutionManager evolution = new CancerEvolutionManager();
        evolution.addSpread(30f);
        require(evolution.checkEvolution(), "Spread reaching 30 should trigger stage 2.");
        require(evolution.getCurrentStage() == 2, "Stage should now be 2.");

        evolution.addSpread(50f);
        require(evolution.checkEvolution(), "Further spread should trigger another stage increase.");
        require(evolution.canEatTCells(), "Stage 4 should allow eating T-cells.");
    }

    private static void testChemoManager() {
        ChemoManager chemo = new ChemoManager();
        require(chemo.update(10f) == 0f, "Chemo should do nothing before activation.");
        chemo.activate();
        float reduction = 0f;
        for (int i = 0; i < 30; i++) {
            reduction = chemo.update(1f);
        }
        require(reduction > 0f, "Chemo should eventually reduce spread.");
        require(chemo.isActive(), "Chemo should remain active once started.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
