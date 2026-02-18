package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;

public class BoundaryWall extends RenderableEntity implements Collidable {

    public BoundaryWall(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.setTexture(TextureFactory.createBoundaryTexture((int) width, (int) height));
    }

    @Override
    public void update(float deltaTime) {
        // static
    }

    @Override
    public void onCollision(Collidable other) {
        // no-op
    }

    @Override
    public boolean isStaticBody() {
        return true;
    }

    @Override
    public int getCollisionLayer() {
        return 1 << 0; // wall layer
    }

    @Override
    public int getCollisionMask() {
        return (1 << 1) | (1 << 2); // player + enemy
    }

    public void dispose() {
        // no-op: textures are shared and owned by DemoTextureFactory
    }
}
