package io.github.some_example_name.engine.collision;

public final class CollisionContact {
    private final float normalX;
    private final float normalY;
    private final float penetration;

    public CollisionContact(float normalX, float normalY, float penetration) {
        this.normalX = normalX;
        this.normalY = normalY;
        this.penetration = penetration;
    }

    public float getNormalX() { return normalX; }
    public float getNormalY() { return normalY; }
    public float getPenetration() { return penetration; }

    public boolean isHorizontal() { return normalX != 0f; }
    public boolean isVertical() { return normalY != 0f; }
}
