package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;

// === ISP FIX: Explicitly implements Collidable ===
public class Wall extends RenderableEntity implements Collidable {

    public Wall(float x, float y, float size) {
        super(x, y, size, size);
        
        // === SRP FIX: Use Factory ===
        this.setTexture(TextureFactory.createWallTexture((int)size));
    }

    @Override
    public void update(float deltaTime) {
        // Walls don't move
    }

    @Override
    public void onCollision(Collidable other) {
        // Walls don't react
    }

    @Override
    public boolean isStaticBody() {
        return true; // Walls are static bodies that don't move
    }

    @Override
    public int getCollisionLayer() { return 1 << 0; } // Layer 1 for walls

    @Override
    public int getCollisionMask() { return (1 << 1) | (1 << 2); } // Collides with players and enemies, but not other walls
    
    public void dispose() {
        // no-op: textures are shared and owned by DemoTextureFactory
    }
}