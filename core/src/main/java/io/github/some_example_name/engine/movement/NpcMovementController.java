package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;

public class NpcMovementController {
    
    private final MovementCalculation helper;

    public NpcMovementController() {
        this.helper = new MovementCalculation();
    }
   
    public void moveEntity(Entity entity, Vector2 velocity, float deltaTime) {
        helper.applyVelocity(entity, velocity, deltaTime);
    }
}

