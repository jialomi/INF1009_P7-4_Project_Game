package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    Rectangle getBounds();
    void onCollision(Collidable other);

    default int getCollisionLayer() {
        return 1; // default layer 1 for all collidables, can be overridden for multiple layers
    }

    default int getCollisionMask() {
        return 0xFFFF_FFFF; // default mask collides with all layers, can be overridden to specify which layers to collide with
    }

    default boolean isStaticBody() {
        return false; // default to dynamic body, can be overridden for static bodies that don't move
    }
}
