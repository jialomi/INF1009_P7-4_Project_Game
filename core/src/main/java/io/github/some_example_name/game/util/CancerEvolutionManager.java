package io.github.some_example_name.game.util;

/**
 * CancerEvolutionManager — global spread % and cancer stage tracker.
 * SOLID/SRP: single source of truth for spread and stage only.
 *
 * Design changes for difficulty:
 * - Stage thresholds raised (harder to evolve)
 * - canEatTCells() requires Stage 4 (not Stage 3)
 *   so T-cells are dangerous for much longer
 */
public class CancerEvolutionManager {

    // Raised thresholds — player must work harder to evolve
    private static final float STAGE_2_THRESHOLD = 30f;
    private static final float STAGE_3_THRESHOLD = 55f;
    private static final float STAGE_4_THRESHOLD = 80f;
    private static final float MAX_SPREAD        = 100f;

    private float currentSpreadPercent = 0f;
    private int   currentStage         = 1;

    // -------------------------------------------------------------------------
    // SPREAD
    // -------------------------------------------------------------------------

    /**
     * Increases spread when a cell is eaten.
     * Call from GameScene when NormalCell or TCell dies.
     */
    public void addSpread(float amount) {
        currentSpreadPercent = Math.min(
            currentSpreadPercent + amount, MAX_SPREAD);
        System.out.println("[Evolution] Spread: "
            + (int) currentSpreadPercent + "%");
    }

    /**
     * Decreases spread when chemo hits.
     * Call from GameScene after ChemoManager.update() returns > 0.
     */
    public void subtractSpread(float amount) {
        currentSpreadPercent = Math.max(0f,
            currentSpreadPercent - amount);
        System.out.println("[Evolution] Chemo reduced spread to: "
            + (int) currentSpreadPercent + "%");
    }

    // -------------------------------------------------------------------------
    // EVOLUTION
    // -------------------------------------------------------------------------

    /**
     * Checks if stage advanced this frame.
     * Call once per frame in GameScene.onUpdate().
     *
     * @return true if stage just increased — triggers burst spawn + BGM change
     */
    public boolean checkEvolution() {
        int previous = currentStage;

        if      (currentSpreadPercent >= STAGE_4_THRESHOLD) currentStage = 4;
        else if (currentSpreadPercent >= STAGE_3_THRESHOLD) currentStage = 3;
        else if (currentSpreadPercent >= STAGE_2_THRESHOLD) currentStage = 2;
        else                                                currentStage = 1;

        if (currentStage > previous) {
            System.out.println("[Evolution] Advanced to Stage " + currentStage + "!");
            return true;
        }
        return false;
    }

    /** Stub — wire up RadioactiveWave entity when implemented */
    public void triggerRadioactiveWave() {
        System.out.println("[Evolution] Radioactive wave at Stage " + currentStage + "!");
    }

    // -------------------------------------------------------------------------
    // QUERIES
    // -------------------------------------------------------------------------

    public float getCurrentSpreadPercent() { return currentSpreadPercent; }
    public int   getCurrentStage()         { return currentStage; }

    /**
     * Tumor can only eat T-cells at Stage 4.
     * This keeps T-cells dangerous for 80% of the run.
     */
    public boolean canEatTCells()        { return currentStage >= 4; }

    public boolean isBodyFullyInfected() { return currentSpreadPercent >= MAX_SPREAD; }

    public void reset() {
        currentSpreadPercent = 0f;
        currentStage         = 1;
        System.out.println("[Evolution] Reset.");
    }

// Add to CancerEvolutionManager.java
/**
 * Returns a description of how strong the immune system
 * is at the current stage — for HUD display.
 */
public String getImmuneStrengthDescription() {
    switch (currentStage) {
        case 1: return "T-Cells: PATROLLING (slow)";
        case 2: return "T-Cells: ALERTED (faster + chemo)";
        case 3: return "T-Cells: AGGRESSIVE (waves)";
        case 4: return "T-Cells: OVERWHELMING (eat them!)";
        default: return "";
    }
}
}