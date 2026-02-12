package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;

public class MovementCalculation {
    
    //Entity Center Calculation
    public Vector2 getEntityCenter(Entity entity) {
        float centerX = entity.getPosition().x + entity.getWidth() / 2;
        float centerY = entity.getPosition().y + entity.getHeight() / 2;
        return new Vector2(centerX, centerY);
    }
    
    // Distance Calculation
    public float getDistanceBetween(Entity a, Entity b) {
        Vector2 centerA = getEntityCenter(a);
        Vector2 centerB = getEntityCenter(b);
        
        float dx = centerB.x - centerA.x;
        float dy = centerB.y - centerA.y;
        
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    // Apply Velocity
    public void applyVelocity(Entity entity, Vector2 velocity, float deltaTime) {
        Vector2 pos = entity.getPosition();
        entity.setPosition(
            pos.x + velocity.x * deltaTime,
            pos.y + velocity.y * deltaTime
        );
    }
}