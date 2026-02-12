package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;

public class MovementManager {
    
    private final PlayerMovementController playerController;
    private final NpcMovementController aiController;
    private final MovementCalculation helper;
    
    public MovementManager() {
        this.playerController = new PlayerMovementController();
        this.aiController = new NpcMovementController();
        this.helper = new MovementCalculation();
    }
    
    // Player Movement
    public void handlePlayerMovement(Entity entity, float speed, float deltaTime) {
        playerController.handleMovement(entity, speed, deltaTime);
    }
    
    // Npc Movement
    public void moveNpc(Entity entity, com.badlogic.gdx.math.Vector2 velocity, float deltaTime) {
        aiController.moveEntity(entity, velocity, deltaTime);
    }
    
    // Helper Methods
    public Vector2 getEntityCenter(Entity entity) {
        return helper.getEntityCenter(entity);
    }
    public float getDistanceBetween(Entity a, Entity b) {
        return helper.getDistanceBetween(a, b);
    }

    // Getters
    public PlayerMovementController getPlayerController() {
        return playerController;
    }

    public NpcMovementController getAIController() {
        return aiController;
    }
    
    public MovementCalculation getHelper() {
        return helper;
    }
}
