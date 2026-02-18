package io.github.some_example_name.engine.collision;

public interface CollisionResolver {
    void resolve(Collidable a, Collidable b, CollisionContact contact);
}