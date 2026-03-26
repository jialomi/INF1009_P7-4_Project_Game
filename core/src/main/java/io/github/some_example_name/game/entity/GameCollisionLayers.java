package io.github.some_example_name.game.entity;

public final class GameCollisionLayers {
    public static final int PLAYER = 1 << 0;
    public static final int HEALTHY_CELL = 1 << 1;
    public static final int IMMUNE_CELL = 1 << 2;
    public static final int PLATFORM = 1 << 3;
    public static final int TRIGGER = 1 << 4;

    private GameCollisionLayers() {
    }
}
