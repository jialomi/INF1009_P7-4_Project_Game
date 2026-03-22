package io.github.some_example_name.engine.io;

public final class OutputConfiguration {
    private final float minWorldWidth;
    private final float minWorldHeight;
    private final float clearRed;
    private final float clearGreen;
    private final float clearBlue;
    private final float clearAlpha;

    public OutputConfiguration() {
        this(800f, 600f, 0f, 0f, 0f, 1f);
    }

    public OutputConfiguration(float minWorldWidth, float minWorldHeight,
                               float clearRed, float clearGreen, float clearBlue, float clearAlpha) {
        if (minWorldWidth <= 0f || minWorldHeight <= 0f) {
            throw new IllegalArgumentException("World dimensions must be > 0.");
        }
        this.minWorldWidth = minWorldWidth;
        this.minWorldHeight = minWorldHeight;
        this.clearRed = clampColor(clearRed);
        this.clearGreen = clampColor(clearGreen);
        this.clearBlue = clampColor(clearBlue);
        this.clearAlpha = clampColor(clearAlpha);
    }

    public float getMinWorldWidth() {
        return minWorldWidth;
    }

    public float getMinWorldHeight() {
        return minWorldHeight;
    }

    public float getClearRed() {
        return clearRed;
    }

    public float getClearGreen() {
        return clearGreen;
    }

    public float getClearBlue() {
        return clearBlue;
    }

    public float getClearAlpha() {
        return clearAlpha;
    }

    private float clampColor(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
