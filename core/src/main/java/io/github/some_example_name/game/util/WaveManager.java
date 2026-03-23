package io.github.some_example_name.game.util;

/**
 * WaveManager — T-cell spawn timing + normal cell wave tracking.
 * SOLID/SRP: only responsible for spawn timing decisions.
 */
public class WaveManager {

    private static final float BASE_INTERVAL = 8f;
    private static final float MIN_INTERVAL  = 2f;
    private static final float STAGE_STEP    = 2f;

    private int   lastTriggeredStage    = 0;
    private float spawnTimer            = 0f;
    private int   normalCellWave        = 1;

    // -------------------------------------------------------------------------
    // T-CELL SPAWNING
    // -------------------------------------------------------------------------

    public boolean shouldSpawnTCell(float delta, int currentStage) {
        float interval = Math.max(MIN_INTERVAL,
            BASE_INTERVAL - ((currentStage - 1) * STAGE_STEP));
        spawnTimer += delta;
        if (spawnTimer >= interval) {
            spawnTimer = 0f;
            return true;
        }
        return false;
    }

    public boolean isNewStageThreshold(int currentStage) {
        if (currentStage > lastTriggeredStage) {
            lastTriggeredStage = currentStage;
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // NORMAL CELL WAVE TRACKING
    // -------------------------------------------------------------------------

    /**
     * Returns true when ALL normal cells in the current wave are dead.
     * GameScene should spawn a fresh batch of 10 when this returns true.
     *
     * @param aliveNormalCellCount current count of living normal cells
     */
    public boolean shouldSpawnNextNormalCellWave(int aliveNormalCellCount) {
        if (aliveNormalCellCount == 0) {
            normalCellWave++;
            System.out.println("[WaveManager] Normal cell wave " + normalCellWave
                + " — spawning next batch!");
            return true;
        }
        return false;
    }

    public int getNormalCellWave() { return normalCellWave; }

    // -------------------------------------------------------------------------
    // RESET
    // -------------------------------------------------------------------------

    public void reset() {
        lastTriggeredStage = 0;
        spawnTimer         = 0f;
        normalCellWave     = 1;
    }
}