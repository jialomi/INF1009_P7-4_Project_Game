package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.util.Validation;

public class MovementManager {
    
    private final MovementCalculation helper;

    public MovementManager() {
        this.helper = new MovementCalculation();
    }

    public void moveByDirection(Entity entity, Vector2 direction, float speed, float deltaTime) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
        if (direction == null) throw new IllegalArgumentException("Direction cannot be null");
        if (speed < 0f) throw new IllegalArgumentException("Speed cannot be negative.");
        Validation.requireValidDelta(deltaTime);

        if (direction.len2() == 0f) {
            return;
        }

        Vector2 normalized = new Vector2(direction).nor().scl(speed * deltaTime);
        entity.setPosition(
            entity.getPositionX() + normalized.x,
            entity.getPositionY() + normalized.y
        );
    }

    public void applyVelocity(Entity entity, Vector2 velocity, float deltaTime) {
        helper.applyVelocity(entity, velocity, deltaTime);
    }

    public void translate(Entity entity, Vector2 delta) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
        if (delta == null) throw new IllegalArgumentException("Delta cannot be null");
        entity.setPosition(entity.getPositionX() + delta.x, entity.getPositionY() + delta.y);
    }
    
    public Vector2 getEntityCenter(Entity entity) {
        return helper.getEntityCenter(entity);
    }

    public float getDistanceBetween(Entity a, Entity b) {
        return helper.getDistanceBetween(a, b);
    }
    
    public MovementCalculation getHelper() {
        return helper;
    }
}
