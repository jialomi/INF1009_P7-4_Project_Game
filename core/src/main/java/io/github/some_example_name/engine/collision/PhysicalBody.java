package io.github.some_example_name.engine.collision;

/**
 * A collidable object whose position can be adjusted by the collision solver.
 */
public interface PhysicalBody extends Collidable {
    float getPositionX();
    float getPositionY();
    void setPosition(float x, float y);
}
