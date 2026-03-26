package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    CollisionShape getCollisionShape();
    void onCollision(Collidable other);
    
    default Rectangle getBroadPhaseBounds() {
        CollisionShape shape = getCollisionShape();
        return shape != null ? shape.getBounds() : null;
    }

    default int getCollisionLayer() { 
        return 1;
    }
    default int getCollisionMask() {
        return 0xFFFF_FFFF;
    }
    default boolean isStaticBody() {
        return false;
    }
    default boolean isSensor() {
        return false;
    }
}
