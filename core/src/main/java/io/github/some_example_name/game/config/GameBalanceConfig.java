package io.github.some_example_name.game.config;

public final class GameBalanceConfig {
    public static final float CHEMO_ACTIVATION_SPREAD = 40.0f;
    public static final float WORLD_WIDTH = 2000.0f;
    public static final float WORLD_HEIGHT = 2000.0f;
    public static final float WORLD_MARGIN = 64.0f;
    public static final float INTERACTION_QUERY_PADDING = 96.0f;
    public static final int SPAWN_ATTEMPTS = 24;
    public static final float SPAWN_PADDING = 12.0f;
    public static final float NORMAL_CELL_MIN_PLAYER_DISTANCE = 140.0f;
    public static final float TCELL_MIN_PLAYER_DISTANCE = 220.0f;
    public static final float TCELL_SEPARATION_RADIUS = 45.0f;
    public static final float TCELL_SEPARATION_STRENGTH = 50.0f;
    public static final float TCELL_RECYCLE_DISTANCE = 900.0f;
    public static final float TCELL_RECYCLE_SECONDS = 10.0f;
    public static final float TCELL_RECYCLE_EDGE_MARGIN = 180.0f;
    public static final float SPREAD_PER_HEALTHY_CELL = 1.0f;

    private GameBalanceConfig() {
    }

    public static float getHealthyCellSpeed(int stage) {
        switch (Math.max(1, Math.min(4, stage))) {
            case 1:
                return 72.0f;
            case 2:
                return 84.0f;
            case 3:
                return 96.0f;
            case 4:
                return 108.0f;
            default:
                return 72.0f;
        }
    }

    public static float getHealthyCellFleeRange(int stage) {
        switch (Math.max(1, Math.min(4, stage))) {
            case 1:
                return 120.0f;
            case 2:
                return 136.0f;
            case 3:
                return 150.0f;
            case 4:
                return 165.0f;
            default:
                return 120.0f;
        }
    }

    public static float getTCellSpeed(int stage) {
        return 120.0f + Math.max(1, stage) * 18.0f;
    }

    public static float getTCellAggressionForSpread(float spreadPercent) {
        float clampedSpread = Math.max(0.0f, Math.min(100.0f, spreadPercent));
        if (clampedSpread <= 75.0f) {
            return clampedSpread / 100.0f;
        }
        return 0.75f;
    }
}
