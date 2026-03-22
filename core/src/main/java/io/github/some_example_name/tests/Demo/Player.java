// core/src/main/java/io/github/some_example_name/tests/Demo/TestPlayer.java
package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionShape;
import io.github.some_example_name.engine.collision.PhysicalBody;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.DynamicInput;
import io.github.some_example_name.engine.movement.MovementManager;

public class Player extends RenderableEntity implements PhysicalBody {

    private static final float HIT_COOLDOWN_SECONDS = 0.75f;
    private float minX = 0f;
    private float minY = 0f;
    private float maxX = Float.NaN;
    private float maxY = Float.NaN;

    private final DynamicInput input;
    private final MovementManager movementManager;
    private float hitCooldown;
    private boolean hitEvent;

    public Player(DynamicInput input, String name, float x, float y) {
        super(x, y, 64, 64);
        if (input == null) {
            throw new IllegalArgumentException("DynamicInput cannot be null");
        }
        this.setTexture(TextureFactory.createPlayerTexture());
        this.input = input;
        this.movementManager = new MovementManager();
    }

    @Override
    public void update(float deltaTime) {
        if (hitCooldown > 0f) hitCooldown -= deltaTime;
        Vector2 direction = new Vector2();
        if (input.isKeyPressed(Input.Keys.W) || input.isKeyPressed(Input.Keys.UP)) direction.y += 1f;
        if (input.isKeyPressed(Input.Keys.S) || input.isKeyPressed(Input.Keys.DOWN)) direction.y -= 1f;
        if (input.isKeyPressed(Input.Keys.A) || input.isKeyPressed(Input.Keys.LEFT)) direction.x -= 1f;
        if (input.isKeyPressed(Input.Keys.D) || input.isKeyPressed(Input.Keys.RIGHT)) direction.x += 1f;
        movementManager.moveByDirection(this, direction, 400f, deltaTime);
        clampToBounds();
    }

    @Override
    public int getCollisionLayer() { return 1 << 1; } // Layer 2 for player

    @Override
    public int getCollisionMask() { return (1 << 0) | (1 << 2); } // Collides with walls and enemies, but not other players

    @Override
    public CollisionShape getCollisionShape() {
        return CollisionShape.rectangle(getBounds());
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Enemy) {
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
