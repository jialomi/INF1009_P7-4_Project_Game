package io.github.some_example_name.game.movement;

import java.util.function.IntPredicate;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class PlayerMovement {
    private final MovementManager movementManager;
    private static final float DASH_DURATION = 0.2f;
    private static final float DASH_COOLDOWN = 1f;
    private static final float DASH_MULTIPLIER = 2f;

    private float dashTimer    = 0f;  // counts down while dashing
    private float cooldownTimer = 0f; // counts down during cooldown

    // Constructor
    public PlayerMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    public void update(float delta) {
        if (dashTimer > 0f) dashTimer -= delta;
        if (cooldownTimer > 0f) cooldownTimer -= delta;
        }

    // Basic movement + dash ability
    public void movePlayer(Entity player, float speed, float delta, IntPredicate isPressed) {
        if (isPressed.test(com.badlogic.gdx.Input.Keys.SHIFT_LEFT) && !isDashing() && cooldownTimer <= 0f) {
            dashTimer    = DASH_DURATION;
            cooldownTimer = DASH_COOLDOWN;
        }
        
        // If dashing, apply multiplier to speed
        float currentSpeed = isDashing() ? speed * DASH_MULTIPLIER : speed;
        movementManager.moveByDirection(player, gatherDirection(isPressed), currentSpeed, delta);
    }

    public boolean isDashing()        { return dashTimer > 0f; }
    public boolean isOnCooldown()     { return cooldownTimer > 0f; }
    public float getCooldownTimer()   { return cooldownTimer; }
    public float getDashCooldown()    { return DASH_COOLDOWN; }

    private com.badlogic.gdx.math.Vector2 gatherDirection(IntPredicate isPressed) {
        com.badlogic.gdx.math.Vector2 direction = new com.badlogic.gdx.math.Vector2();
        if (isPressed.test(com.badlogic.gdx.Input.Keys.W) || isPressed.test(com.badlogic.gdx.Input.Keys.UP)) direction.y += 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.S) || isPressed.test(com.badlogic.gdx.Input.Keys.DOWN)) direction.y -= 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.A) || isPressed.test(com.badlogic.gdx.Input.Keys.LEFT)) direction.x -= 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.D) || isPressed.test(com.badlogic.gdx.Input.Keys.RIGHT)) direction.x += 1f;
        return direction;
    }
}
