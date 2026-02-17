package io.github.some_example_name.engine.util;

public class Validation {
    private Validation() {}

    public static void requireValidDelta(float delta) {
        if (Float.isNaN(delta) || Float.isInfinite(delta) || delta < 0f) {
            throw new IllegalArgumentException("Delta time must be finite and >= 0.");
        }
    }
}
