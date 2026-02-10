package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;

/**
 * MovementManager - Coordinator for all movement-related operations
 * 
 * Design Pattern: Facade Pattern
 * Single Responsibility: Provide unified interface to movement subsystems
 * 
 * This class coordinates three specialized controllers:
 * - PlayerMovementController: Keyboard-based player movement
 * - AIMovementController: AI behaviors (wander, chase, flee)
 * - MovementHelper: Utility calculations (distance, direction)
 * 
 * Benefits of this architecture:
 * ✓ Each controller has ONE clear responsibility (SOLID)
 * ✓ Easy to test individual controllers
 * ✓ Easy to extend with new movement types
 * ✓ Simple facade for users who want convenience
 */
public class MovementManager {
    
    // Specialized subsystems
    private final PlayerMovementController playerController;
    private final NpcMovementController aiController;
    private final MovementCalculation helper;
    
    /**
     * Constructor - Initialize all movement subsystems
     */
    public MovementManager() {
        this.playerController = new PlayerMovementController();
        this.aiController = new NpcMovementController();
        this.helper = new MovementCalculation();
    }
    
    // ============================================================================
    // PLAYER MOVEMENT (delegates to PlayerMovementController)
    // ============================================================================
    
    /**
     * Handle WASD keyboard movement
     */
    public void handleWASDMovement(Entity entity, float speed, float deltaTime) {
        playerController.handleWASDMovement(entity, speed, deltaTime);
    }
    
    /**
     * Handle WASD with normalized diagonal movement
     */
    public void handleWASDMovementNormalized(Entity entity, float speed, float deltaTime) {
        playerController.handleWASDMovementNormalized(entity, speed, deltaTime);
    }
    
    
    // ============================================================================
    // AI MOVEMENT (delegates to AIMovementController)
    // ============================================================================
    
    /**
     * Make NPC wander randomly with smooth direction changes
     */
    public void wanderRandomly(Entity entity, float speed, Vector2 currentDirection, 
                               float[] directionTimer, float changeInterval, float deltaTime) {
        aiController.wanderRandomly(entity, speed, currentDirection, directionTimer, 
                                   changeInterval, deltaTime);
    }
    
    /**
     * Wander by moving to random points
     */
    public void wanderToRandomPoints(Entity entity, float speed, Vector2 targetPos,
                                     float stopDistance, float minX, float maxX, 
                                     float minY, float maxY, float deltaTime) {
        aiController.wanderToRandomPoints(entity, speed, targetPos, stopDistance, 
                                         minX, maxX, minY, maxY, deltaTime);
    }
    
    /**
     * Chase a target entity
     */
    public void chaseTarget(Entity entity, Entity target, float speed, float deltaTime) {
        aiController.chaseTarget(entity, target, speed, deltaTime);
    }
    
    /**
     * Chase target only if within range
     */
    public boolean chaseIfInRange(Entity entity, Entity target, float speed, 
                                  float detectionRange, float deltaTime) {
        return aiController.chaseIfInRange(entity, target, speed, detectionRange, deltaTime);
    }
    
    /**
     * Flee from a threat
     */
    public void fleeFromThreat(Entity entity, Entity threat, float speed, float deltaTime) {
        aiController.fleeFromThreat(entity, threat, speed, deltaTime);
    }
    
    /**
     * Flee only if threat is too close
     */
    public boolean fleeIfTooClose(Entity entity, Entity threat, float speed, 
                                  float dangerRange, float deltaTime) {
        return aiController.fleeIfTooClose(entity, threat, speed, dangerRange, deltaTime);
    }
    
    // ============================================================================
    // HELPER UTILITIES (delegates to MovementHelper)
    // ============================================================================
    
    /**
     * Get entity center position
     */
    public Vector2 getEntityCenter(Entity entity) {
        return helper.getEntityCenter(entity);
    }
    
    /**
     * Calculate distance between two entities
     */
    public float getDistanceBetween(Entity a, Entity b) {
        return helper.getDistanceBetween(a, b);
    }
    
    /**
     * Get direction from one entity to another
     */
    public Vector2 getDirectionTo(Entity from, Entity to) {
        return helper.getDirectionTo(from, to);
    }
    
    /**
     * Get direction away from target
     */
    public Vector2 getDirectionAwayFrom(Entity from, Entity awayFrom) {
        return helper.getDirectionAwayFrom(from, awayFrom);
    }
    
    /**
     * Check if entities are within range
     */
    public boolean isInRange(Entity a, Entity b, float range) {
        return helper.isInRange(a, b, range);
    }
    
    /**
     * Calculate angle from one entity to another
     */
    public float getAngleTo(Entity from, Entity to) {
        return helper.getAngleTo(from, to);
    }
    
    /**
     * Apply velocity to entity
     */
    public void applyVelocity(Entity entity, Vector2 velocity, float deltaTime) {
        Vector2 pos = entity.getPosition();
        entity.setPosition(
            pos.x + velocity.x * deltaTime,
            pos.y + velocity.y * deltaTime
        );
    }
    
    // ============================================================================
    // DIRECT SUBSYSTEM ACCESS (for advanced usage)
    // ============================================================================
    
    /**
     * Get direct access to PlayerMovementController
     * Use for advanced player movement operations
     */
    public PlayerMovementController getPlayerController() {
        return playerController;
    }
    
    /**
     * Get direct access to AIMovementController
     * Use for advanced AI movement operations
     */
    public NpcMovementController getAIController() {
        return aiController;
    }
    
    /**
     * Get direct access to MovementHelper
     * Use for advanced calculations
     */
    public MovementCalculation getHelper() {
        return helper;
    }
}
