package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;
import java.util.function.IntPredicate;

import io.github.some_example_name.engine.entity.Entity;

public class MovementManager {
    
    private PlayerMovementController playerController;
    private NpcMovementController npcController;
    private MovementCalculation helper;
    
    public MovementManager() {
        this.playerController = new PlayerMovementController();
        this.npcController = new NpcMovementController();
        this.helper = new MovementCalculation();
    }
    
    // Player Movement - Main method
    public void handlePlayerMovement(Entity entity, float speed, float deltaTime, IntPredicate isPressed) {
        playerController.handleMovement(entity, speed, deltaTime, isPressed);
    }

    // Player Movement - Backward-compatible path
    public void handlePlayerMovement(Entity entity, float speed, float deltaTime) {
        playerController.handleMovement(entity, speed, deltaTime);
    }
    
    // Npc Movement
    public void moveNpc(Entity entity, com.badlogic.gdx.math.Vector2 velocity, float deltaTime) {
        npcController.moveEntity(entity, velocity, deltaTime);
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

    public NpcMovementController getNpcController() {
        return npcController;
    }
    
    public MovementCalculation getHelper() {
        return helper;
    }
}
