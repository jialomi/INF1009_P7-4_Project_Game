package io.github.some_example_name.game.movement;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class PlayerMovement {
    private final MovementManager movementManager;
    private static final float DASH_DURATION = 0.2f;
    private static final float DASH_COOLDOWN = 1f;
    private static final float DASH_MULTIPLIER = 2f;

    private float dashTimer = 0f; // counts down while dashing
    private float cooldownTimer = 0f; // counts down during cooldown

    // Constructor
    public PlayerMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    public void update(float delta) {
        if (dashTimer > 0f)
            dashTimer -= delta;
        if (cooldownTimer > 0f)
            cooldownTimer -= delta;
    }

    // basic movement + dash ability
    public void movePlayer(Entity player, float speed, float delta, com.badlogic.gdx.math.Vector2 direction,
            boolean isDashing) {
        if (isDashing && !isDashing() && cooldownTimer <= 0f) {
            dashTimer = DASH_DURATION;
            cooldownTimer = DASH_COOLDOWN;
        }

        // if dashing, apply multiplier to speed
        float currentSpeed = isDashing() ? speed * DASH_MULTIPLIER : speed;
        movementManager.moveByDirection(player, direction, currentSpeed, delta);
    }

    public boolean isDashing() {
        return dashTimer > 0f;
    }

    public boolean isOnCooldown() {
        return cooldownTimer > 0f;
    }

    public float getCooldownTimer() {
        return cooldownTimer;
    }

    public float getDashCooldown() {
        return DASH_COOLDOWN;
    }
}
