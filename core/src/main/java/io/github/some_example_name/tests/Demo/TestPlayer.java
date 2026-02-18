// core/src/main/java/io/github/some_example_name/tests/Demo/TestPlayer.java
package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.movement.MovementManager;

public class TestPlayer extends RenderableEntity implements Collidable {

    private static final float HIT_COOLDOWN_SECONDS = 0.75f;
    private float minX = 0f;
    private float minY = 0f;
    private float maxX = Float.NaN;
    private float maxY = Float.NaN;

    private final MovementManager movementManager;
    private float hitCooldown;
    private boolean hitEvent;

    public TestPlayer(String name, float x, float y) {
        super(x, y, 64, 64);
        this.setTexture(DemoTextureFactory.createPlayerTexture());
        this.movementManager = new MovementManager();
    }

    @Override
    public void update(float deltaTime) {
        if (hitCooldown > 0f) hitCooldown -= deltaTime;
        movementManager.handlePlayerMovement(this, 400f, deltaTime);
        clampToBounds();
    }

    @Override
    public int getCollisionLayer() { return 1 << 1; } // Layer 2 for player

    @Override
    public int getCollisionMask() { return (1 << 0) | (1 << 2); } // Collides with walls and enemies, but not other players

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof TestEnemy) {
            if (hitCooldown <= 0f) {
                hitEvent = true;
                hitCooldown = HIT_COOLDOWN_SECONDS;
            }
        }
    }

    public boolean consumeHitEvent() {
        if (hitEvent) {
            hitEvent = false;
            return true;
        }
        return false;
    }

    public boolean isInvulnerable() {
        return hitCooldown > 0f;
    }

    public void setMovementBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    private void clampToBounds() {
        if (Float.isNaN(maxX) || Float.isNaN(maxY)) return;

        float clampedX = Math.max(minX, Math.min(getPositionX(), maxX - getWidth()));
        float clampedY = Math.max(minY, Math.min(getPositionY(), maxY - getHeight()));
        setPosition(clampedX, clampedY);
    }

    public void dispose() {
        // no-op: textures are shared and owned by DemoTextureFactory
    }
}
