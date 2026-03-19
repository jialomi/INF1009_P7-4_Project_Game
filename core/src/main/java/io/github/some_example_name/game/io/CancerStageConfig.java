package io.github.some_example_name.game.io;

public class CancerStageConfig {
    public float requiredSpreadPercent;
    public boolean canKillTCells;
    public boolean radioactiveWaveActive;

    public CancerStageConfig(float requiredSpreadPercent, boolean canKillTCells, boolean radioactiveWaveActive) {
        this.requiredSpreadPercent = requiredSpreadPercent;
        this.canKillTCells = canKillTCells;
        this.radioactiveWaveActive = radioactiveWaveActive;
    }
}