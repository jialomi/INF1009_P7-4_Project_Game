package io.github.some_example_name.game.util;

import java.util.Locale;

/**
 * Tracks global spread percentage and stage progression for the run.
 * Stage 5 is a short terminal phase near full infection where the immune
 * system can no longer damage the tumor.
 */
public class CancerEvolutionManager {
    private static final float STAGE_2_THRESHOLD = 30.0f;
    private static final float STAGE_3_THRESHOLD = 55.0f;
    private static final float STAGE_4_THRESHOLD = 80.0f;
    private static final float TERMINAL_STAGE_THRESHOLD = 90.0f;
    private static final float MAX_SPREAD = 100.0f;

    private float currentSpreadPercent = 0f;
    private int currentStage = 1;

    public void addSpread(float amount) {
        currentSpreadPercent = roundToTenth(Math.min(currentSpreadPercent + amount, MAX_SPREAD));
        System.out.println("[Evolution] Spread: " + formatSpread(currentSpreadPercent));
    }

    public void subtractSpread(float amount) {
        currentSpreadPercent = roundToTenth(Math.max(0f, currentSpreadPercent - amount));
        System.out.println("[Evolution] Chemo reduced spread to: " + formatSpread(currentSpreadPercent));
    }

    public boolean checkEvolution() {
        int previous = currentStage;

        // Spread can move both directions because chemo is permanent once it starts,
        // so stage is recomputed from the current percentage instead of only increasing.
        if (currentSpreadPercent >= TERMINAL_STAGE_THRESHOLD) {
            currentStage = 5;
        } else if (currentSpreadPercent >= STAGE_4_THRESHOLD) {
            currentStage = 4;
        } else if (currentSpreadPercent >= STAGE_3_THRESHOLD) {
            currentStage = 3;
        } else if (currentSpreadPercent >= STAGE_2_THRESHOLD) {
            currentStage = 2;
        } else {
            currentStage = 1;
        }

        if (currentStage > previous) {
            System.out.println("[Evolution] Advanced to Stage " + currentStage + "!");
            return true;
        }
        return false;
    }

    public float getCurrentSpreadPercent() {
        return currentSpreadPercent;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public boolean canEatTCells() {
        return currentStage >= 4;
    }

    public boolean isTerminalStage() {
        return currentStage >= 5;
    }

    public boolean isBodyFullyInfected() {
        return currentSpreadPercent >= MAX_SPREAD;
    }

    public void reset() {
        currentSpreadPercent = 0f;
        currentStage = 1;
        System.out.println("[Evolution] Reset.");
    }

    public String getImmuneStrengthDescription() {
        switch (currentStage) {
            case 1:
                return "T-Cells: PATROLLING";
            case 2:
                return "T-Cells: ALERTED";
            case 3:
                return "T-Cells: AGGRESSIVE ";
            case 4:
                return "T-Cells: OVERWHELMING";
            case 5:
                return "T-Cells: COLLAPSED";
            default:
                return "";
        }
    }

    private float roundToTenth(float value) {
        return Math.round(value * 10.0f) / 10.0f;
    }

    private String formatSpread(float value) {
        return String.format(Locale.US, "%.1f%%", value);
    }
}
