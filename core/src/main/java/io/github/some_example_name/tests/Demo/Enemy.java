// core/src/main/java/io/github/some_example_name/tests/Demo/TestEnemy.java
package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.movement.MovementManager;
import com.badlogic.gdx.math.Rectangle;

public class Enemy extends RenderableEntity implements Collidable {

    private final TextureRegion redTexture;
    private final TextureRegion yellowTexture;
    private final MovementManager movementManager;

    private float speed = 150f;
    private float soundTimer = 0f;
    private float driftDir = Math.random() < 0.5 ? -1f : 1f; // Randomly start drifting left or right
    private float driftSpeed = 80f;
    private Collidable lastBounceWall;
    private float bounceLockTimer = 0f;

    public Enemy(String name, float x, float y) {
        super(x, y, 48, 48);
        redTexture = TextureFactory.createEnemyTexture(false);
        yellowTexture = TextureFactory.createEnemyTexture(true);
        this.setTexture(redTexture);
        this.movementManager = new MovementManager();
    }

    public void setSpeed(float speed) {
        this.speed = Math.max(40f, speed);
    }

    @Override
    public void update(float deltaTime) {
        if (soundTimer > 0f) soundTimer -= deltaTime;
        if (bounceLockTimer > 0f) bounceLockTimer -= deltaTime;
        this.setTexture(redTexture);

        Vector2 velocity = new Vector2(driftDir * driftSpeed, -speed);
        movementManager.moveNpc(this, velocity, deltaTime);

        if (getPositionY() < -50f) {
            float newX = (float) (Math.random() * 750f);
            setPosition(newX, 650);
            driftDir = Math.random() < 0.5 ? -1f : 1f; // Randomize drift direction on respawn
        }
    }

    @Override
    public int getCollisionLayer() { return 1 << 2; } // Layer 3 for enemies

    @Override
    public int getCollisionMask() { return (1 << 0) | (1 << 1); } // Collides with walls and player, but not other enemies

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player) {
            this.setTexture(yellowTexture);
            if (soundTimer <= 0f) {
                IOManager.getInstance().getAudio().playSound("crash.mp3");
                soundTimer = 0.2f;
            }
            return;
        }

        if (other instanceof Wall || other instanceof BoundaryWall) {
            if (isSideContact(other) && isMovingTowardWall(other)) {
                if (bounceLockTimer <= 0f || other != lastBounceWall) {
                    driftDir *= -1f;
                    lastBounceWall = other;
                    bounceLockTimer = 0.12f;
                }
            }
        }
    }

    private boolean isSideContact(Collidable wallLike) {
        Rectangle e = getBounds();
        Rectangle w = wallLike.getBounds();
        if (e == null || w == null) return false;

        float distToLeftFace  = Math.abs((e.x + e.width) - w.x);
        float distToRightFace = Math.abs(e.x - (w.x + w.width));
        float distToTopFace   = Math.abs(e.y - (w.y + w.height));
        float distToBottomFace= Math.abs((e.y + e.height) - w.y);

        float sideDist = Math.min(distToLeftFace, distToRightFace);
        float vertDist = Math.min(distToTopFace, distToBottomFace);

        return sideDist < vertDist;
    }

    private boolean isMovingTowardWall(Collidable wallLike) {
        Rectangle e = getBounds();
        Rectangle w = wallLike.getBounds();
        if (e == null || w == null) return false;

        float eCx = e.x + e.width * 0.5f;
        float wCx = w.x + w.width * 0.5f;

        // bounce only if drifting toward wall face
        return (driftDir > 0f && wCx > eCx) || (driftDir < 0f && wCx < eCx);
    }

    public void dispose() {
        // no-op: textures are shared and owned by DemoTextureFactory
    }
}
