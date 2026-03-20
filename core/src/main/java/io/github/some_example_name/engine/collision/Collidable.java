package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;

public interface Collidable {
    Rectangle getBounds();
    void onCollision(Collidable other);

    /** Override this to use a non-rectangular collision shape. */
    default CollisionShape getCollisionShape() {
        return CollisionShape.rectangle(getBounds());
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
}
