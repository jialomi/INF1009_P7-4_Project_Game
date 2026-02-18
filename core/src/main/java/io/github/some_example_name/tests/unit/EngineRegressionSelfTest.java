package io.github.some_example_name.tests.unit;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionManager;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.DynamicInput;
import java.util.concurrent.atomic.AtomicInteger;

public final class EngineRegressionSelfTest {
    public static void main(String[] args) {
        testInputFlush();
        testOverlapSeparated();
        testResolveOncePerFrame();
        testLayerMaskFiltering();
        testSceneTransitionInputNoChain();
        testWallBounceLockPreventsDoubleBounce();
        System.out.println("All regression checks passed.");
    }

    private static void testInputFlush() {
        DynamicInput input = new DynamicInput();
        input.initialize();

        final int KEY_R = 82;
        final int KEY_P = 80;

        input.keyDown(KEY_R);
        require(input.isKeyJustPressed(KEY_R), "R should be just-pressed");

        input.clearJustPressed();
        input.keyDown(KEY_P);
        require(input.isKeyJustPressed(KEY_P), "P should be just-pressed after flush");
        require(!input.isKeyJustPressed(KEY_R), "R should not remain just-pressed after flush");
    }

    private static void testOverlapSeparated() {
        CollisionManager cm = new CollisionManager();
        EntityBox enemy = new EntityBox(100, 100, 20, 20, false);
        EntityBox wall = new EntityBox(110, 100, 20, 20, true);
        cm.addCollidable(enemy);
        cm.addCollidable(wall);
        cm.update();
        require(!enemy.getBounds().overlaps(wall.getBounds()), "enemy must be separated from wall");
    }

    private static void testResolveOncePerFrame() {
        AtomicInteger calls = new AtomicInteger(0);
        CollisionManager cm = new CollisionManager((a, b, c) -> calls.incrementAndGet());
        Box a = new Box(0, 0, 20, 20, false);
        Box b = new Box(10, 0, 20, 20, true);
        cm.addCollidable(a);
        cm.addCollidable(b);
        cm.update();
        require(calls.get() == 1, "resolver should be called once per pair per frame");
    }

    private static void testLayerMaskFiltering() {
        AtomicInteger calls = new AtomicInteger(0);
        CollisionManager cm = new CollisionManager((a, b, c) -> calls.incrementAndGet());

        MaskBox a = new MaskBox(0, 0, 20, 20, 1 << 1, 1 << 2, false);
        MaskBox b = new MaskBox(10, 0, 20, 20, 1 << 0, 1 << 3, true); // mask/layer mismatch

        cm.addCollidable(a);
        cm.addCollidable(b);
        cm.update();

        require(calls.get() == 0, "Layer/mask mismatch should block collision callback");
    }

    private static void testSceneTransitionInputNoChain() {
        DynamicInput input = new DynamicInput();
        input.initialize();

        final int KEY_R = 82;
        final int KEY_P = 80;

        input.keyDown(KEY_R);
        require(input.isKeyJustPressed(KEY_R), "R should be just-pressed before transition");

        // Mimics SceneManager.setActive() input flush
        input.clearJustPressed();

        require(!input.isKeyJustPressed(KEY_R), "R should be cleared after transition flush");

        input.keyDown(KEY_P);
        require(input.isKeyJustPressed(KEY_P), "P should be fresh just-pressed after flush");
        require(!input.isKeyJustPressed(KEY_R), "R must not chain into next scene");
    }

    private static void testWallBounceLockPreventsDoubleBounce() {
        BounceEnemyHarness enemy = new BounceEnemyHarness(40f, 40f, 20f, 20f, 1f);
        Box rightWall = new Box(60f, 40f, 20f, 20f, true);

        enemy.onCollision(rightWall);
        float afterFirst = enemy.getDriftDir();

        enemy.onCollision(rightWall); // same wall, same frame
        float afterSecond = enemy.getDriftDir();

        require(Math.abs(afterFirst - afterSecond) < 0.0001f,
            "Enemy should not double-bounce on same wall contact");

        enemy.advance(0.2f); // cooldown expires

        Box leftWall = new Box(20f, 40f, 20f, 20f, true);
        enemy.onCollision(leftWall);

        require(enemy.getDriftDir() > 0f,
            "Enemy should bounce again on opposite wall after lock expires");
    }

    private static void require(boolean ok, String msg) {
        if (!ok) throw new IllegalStateException(msg);
    }

    private static final class Box implements Collidable {
        private final Rectangle bounds;
        private final boolean isStatic;

        Box(float x, float y, float w, float h, boolean isStatic) {
            this.bounds = new Rectangle(x, y, w, h);
            this.isStatic = isStatic;
        }

        @Override public Rectangle getBounds() { return bounds; }
        @Override public void onCollision(Collidable other) {}
        @Override public boolean isStaticBody() { return isStatic; }
        @Override public int getCollisionLayer() { return 1; }
        @Override public int getCollisionMask() { return 1; }
    }

    private static final class EntityBox extends Entity implements Collidable {
        private final Rectangle bounds;
        private final boolean isStatic;
        private final float width;
        private final float height;

        EntityBox(float x, float y, float w, float h, boolean isStatic) {
            super(x, y);
            this.bounds = new Rectangle(x, y, w, h);
            this.isStatic = isStatic;
            this.width = w;
            this.height = h;
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            bounds.setPosition(x, y);
        }

        @Override
        public Rectangle getBounds() {
            bounds.setPosition(getPositionX(), getPositionY());
            return bounds;
        }

        @Override public void onCollision(Collidable other) {}
        @Override public boolean isStaticBody() { return isStatic; }
        @Override public int getCollisionLayer() { return 1; }
        @Override public int getCollisionMask() { return 1; }

        @Override public void update(float deltaTime) {}
        @Override public com.badlogic.gdx.graphics.g2d.TextureRegion getTexture() { return null; }
        @Override public float getWidth() { return width; }
        @Override public float getHeight() { return height; }
    }

    private static final class MaskBox implements Collidable {
        private final Rectangle bounds;
        private final int layer;
        private final int mask;
        private final boolean isStatic;

        MaskBox(float x, float y, float w, float h, int layer, int mask, boolean isStatic) {
            this.bounds = new Rectangle(x, y, w, h);
            this.layer = layer;
            this.mask = mask;
            this.isStatic = isStatic;
        }

        @Override public Rectangle getBounds() { return bounds; }
        @Override public void onCollision(Collidable other) {}
        @Override public int getCollisionLayer() { return layer; }
        @Override public int getCollisionMask() { return mask; }
        @Override public boolean isStaticBody() { return isStatic; }
    }

    private static final class BounceEnemyHarness implements Collidable {
        private final Rectangle bounds;
        private float driftDir;
        private Collidable lastBounceWall;
        private float bounceLockTimer;

        BounceEnemyHarness(float x, float y, float w, float h, float driftDir) {
            this.bounds = new Rectangle(x, y, w, h);
            this.driftDir = driftDir;
        }

        void advance(float dt) {
            if (bounceLockTimer > 0f) bounceLockTimer -= dt;
        }

        float getDriftDir() {
            return driftDir;
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public void onCollision(Collidable other) {
            if (isSideContact(other) && isMovingTowardWall(other)) {
                if (bounceLockTimer <= 0f || other != lastBounceWall) {
                    driftDir *= -1f;
                    lastBounceWall = other;
                    bounceLockTimer = 0.12f;
                }
            }
        }

        private boolean isSideContact(Collidable wallLike) {
            Rectangle e = getBounds();
            Rectangle w = wallLike.getBounds();
            if (e == null || w == null) return false;

            float distToLeftFace = Math.abs((e.x + e.width) - w.x);
            float distToRightFace = Math.abs(e.x - (w.x + w.width));
            float distToTopFace = Math.abs(e.y - (w.y + w.height));
            float distToBottomFace = Math.abs((e.y + e.height) - w.y);

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
            return (driftDir > 0f && wCx > eCx) || (driftDir < 0f && wCx < eCx);
        }
    }
}
