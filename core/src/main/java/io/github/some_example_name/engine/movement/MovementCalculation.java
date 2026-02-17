package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.util.Validation;

public class MovementCalculation {
    
    //Entity Center Calculation
    public Vector2 getEntityCenter(Entity entity) {
        requireEntity(entity, "entity");
        float centerX = entity.getPosition().x + entity.getWidth() / 2;
        float centerY = entity.getPosition().y + entity.getHeight() / 2;
        return new Vector2(centerX, centerY);
    }
    
    // Distance Calculation
    public float getDistanceBetween(Entity a, Entity b) {
        requireEntity(a, "a");
        requireEntity(b, "b");

        Vector2 centerA = getEntityCenter(a);
        Vector2 centerB = getEntityCenter(b);
        
        float dx = centerB.x - centerA.x;
        float dy = centerB.y - centerA.y;
        
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    // Apply Velocity
    public void applyVelocity(Entity entity, Vector2 velocity, float deltaTime) {
        requireEntity(entity, "entity");
        if (velocity == null) throw new IllegalArgumentException("Velocity cannot be null.");
        Validation.requireValidDelta(deltaTime);

        entity.setPosition(
            entity.getPosition().x + velocity.x * deltaTime,
            entity.getPosition().y + velocity.y * deltaTime
        );
    }

    // Helper method for null checks
    private static void requireEntity(Entity entity, String name) {
        if (entity == null) {
            throw new IllegalArgumentException(name + " cannot be null.");
        }
    }
}