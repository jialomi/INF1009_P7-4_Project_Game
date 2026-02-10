package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;

// === ISP FIX: Explicitly implements Collidable ===
public class TestWall extends RenderableEntity implements Collidable {

    public TestWall(float x, float y, float size) {
        super(x, y, size, size);
        
        // === SRP FIX: Use Factory ===
        this.setTexture(DemoTextureFactory.createWallTexture((int)size));
    }

    @Override
    public void update(float deltaTime) {
        // Walls don't move
    }

    @Override
    public void onCollision(Collidable other) {
        // Walls don't react
    }
    
    public void dispose() {
        if (getTexture() != null) getTexture().getTexture().dispose();
    }
}