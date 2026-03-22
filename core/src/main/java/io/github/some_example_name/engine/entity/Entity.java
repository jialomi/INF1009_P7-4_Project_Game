package io.github.some_example_name.engine.entity;

import java.util.UUID;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract base class for all entities in the Abstract Engine.
 *
 * This represents any object that exists in the simulation world.
 * Entities are generic and context-free - they don't know about
 * specific games like rockets, cars, etc.
 */
public abstract class Entity {

    // ===== ATTRIBUTES =====
    private final UUID id;              // Unique identifier (never changes!)
    private float positionX;            // X coordinate in world space
    private float positionY;            // Y coordinate in world space
    private float previousPositionX;    // X coordinate at previous simulation step
    private float previousPositionY;    // Y coordinate at previous simulation step
    private float velocityX;            // Velocity in X direction
    private float velocityY;            // Velocity in Y direction
    private float width;                // Generic spatial width
    private float height;               // Generic spatial height
    private boolean active;             // Whether this entity is active/alive

    // Create a new entity at the origin (0,0).
    public Entity() {
        this.id = UUID.randomUUID();
        this.positionX = 0.0f;
        this.positionY = 0.0f;
        this.previousPositionX = 0.0f;
        this.previousPositionY = 0.0f;
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.width = 0.0f;
        this.height = 0.0f;
        this.active = true;
    }

    // Creates a new Entity at a specific position
    public Entity(float positionX, float positionY) {
        this.id = UUID.randomUUID();
        this.positionX = positionX;
        this.positionY = positionY;
        this.previousPositionX = positionX;
        this.previousPositionY = positionY;
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.width = 0.0f;
        this.height = 0.0f;
        this.active = true;
    }

    public Entity(float positionX, float positionY, float width, float height) {
        this.id = UUID.randomUUID();
        this.positionX = positionX;
        this.positionY = positionY;
        this.previousPositionX = positionX;
        this.previousPositionY = positionY;
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    // ===== ABSTRACT METHODS =====

    /**
     * Updates the entity's state.
     * Subclasses MUST implement their specific update logic.
     * 
     * @param deltaTime Time elapsed since last update (in seconds)
     */
    public abstract void update(float deltaTime);

    // ===== HELPER METHODS =====

    /**
     * Applies basic movement based on current velocity.
     * This is a helper method that subclasses CAN use if they want.
     * Not required!
     * 
     * @param deltaTime Time elapsed since last update
     */
    protected void applyMovement(float deltaTime) {
        positionX += velocityX * deltaTime;
        positionY += velocityY * deltaTime;
    }

    // ===== GETTERS =====

    public UUID getId() {
        return id;
    }

    public float getPositionX() {
        return positionX;
    }
    
    public float getPositionY() {
        return positionY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public boolean isActive() {
        return active;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getPosition() {
        return new Vector2(positionX, positionY);
    }

    public float getPreviousPositionX() {
        return previousPositionX;
    }

    public float getPreviousPositionY() {
        return previousPositionY;
    }

    public float getInterpolatedPositionX(float alpha) {
        float clampedAlpha = clamp01(alpha);
        return previousPositionX + (positionX - previousPositionX) * clampedAlpha;
    }

    public float getInterpolatedPositionY(float alpha) {
        float clampedAlpha = clamp01(alpha);
        return previousPositionY + (positionY - previousPositionY) * clampedAlpha;
    }

    // ===== SETTERS =====

    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void setVelocity(float vx, float vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void capturePreviousState() {
        this.previousPositionX = this.positionX;
        this.previousPositionY = this.positionY;
    }

    public void snapInterpolation() {
        capturePreviousState();
    }

    private float clamp01(float alpha) {
        return Math.max(0f, Math.min(1f, alpha));
    }

}
