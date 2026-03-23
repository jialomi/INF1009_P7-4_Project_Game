package io.github.some_example_name.game.util;

/**
 * ChemoManager — time-based % infection reduction (chemotherapy).
 * SOLID/SRP: only responsible for chemo timing.
 *
 * Design changes for difficulty:
 * - Chemo activates at Stage 1 (not Stage 2)
 * - Hits every 15s instead of 20s
 * - Reduces spread by 8% instead of 5%
 * - Warn player 5s before (not 3s) — gives reaction time but keeps pressure
 */
public class ChemoManager {

    private static final float CHEMO_INTERVAL  = 30f; // was 15f
    private static final float CHEMO_REDUCTION = 5f;  // was 8f
    private static final float WARN_BEFORE     = 5f;

    private float   chemoTimer  = 0f;
    private boolean active      = false;
    private boolean warningSent = false;

    /** Activates chemo. Call when Stage 1 is reached (i.e. game start). */
    public void activate() {
        if (!active) {
            active = true;
            System.out.println("[Chemo] Chemotherapy activated!");
        }
    }

    public void reset() {
        chemoTimer  = 0f;
        active      = false;
        warningSent = false;
    }

    /**
     * Advances the chemo timer.
     *
     * @param delta seconds since last frame
     * @return spread % to subtract this frame — pass to
     *         CancerEvolutionManager.subtractSpread()
     */
    public float update(float delta) {
        if (!active) return 0f;

        chemoTimer += delta;

        if (!warningSent && chemoTimer >= CHEMO_INTERVAL - WARN_BEFORE) {
            warningSent = true;
            System.out.println("[Chemo] WARNING — chemo incoming in "
                + (int) WARN_BEFORE + "s!");
        }

        if (chemoTimer >= CHEMO_INTERVAL) {
            chemoTimer  = 0f;
            warningSent = false;
            System.out.println("[Chemo] Hit! Spread reduced by " + CHEMO_REDUCTION + "%");
            return CHEMO_REDUCTION;
        }

        return 0f;
    }

    public boolean isActive()              { return active; }
    public float getTimeUntilNextChemo()   { return Math.max(0f, CHEMO_INTERVAL - chemoTimer); }
}