package io.github.some_example_name.game.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class PlayerMovement {
    private static final float BASE_SPEED = 340f;
    private static final float DASH_DURATION = 0.18f;
    private static final float DASH_COOLDOWN = 0.9f;
    private static final float DASH_MULTIPLIER = 2.1f;

    private final MovementManager movementManager;
    private float dashTimer;
    private float cooldownTimer;

    public PlayerMovement() {
        this(new MovementManager());
    }

    public PlayerMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    public void update(Entity player, Vector2 direction, boolean dashPressed, float deltaTime) {
        if (dashTimer > 0f) {
            dashTimer -= deltaTime;
        }
        if (cooldownTimer > 0f) {
            cooldownTimer -= deltaTime;
        }
        if (dashPressed && !isDashing() && cooldownTimer <= 0f) {
            dashTimer = DASH_DURATION;
            cooldownTimer = DASH_COOLDOWN;
        }
        float speed = isDashing() ? BASE_SPEED * DASH_MULTIPLIER : BASE_SPEED;
        movementManager.moveByDirection(player, direction, speed, deltaTime);
    }

    public boolean isDashing() {
        return dashTimer > 0f;
    }
}
