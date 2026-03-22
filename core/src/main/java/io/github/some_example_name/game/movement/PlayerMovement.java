package io.github.some_example_name.game.movement;

import java.util.function.IntPredicate;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class PlayerMovement {
    private final MovementManager movementManager;
    private boolean isDashing = false;
    private float dashTimer = 0f;
    private static final float DASH_DURATION = 0.2f;
    private static final float DASH_MULTIPLIER = 3f;

    // Constructor
    public PlayerMovement(MovementManager movementManager) {
        this.movementManager = movementManager;
    }

    // Basic movement
    public void movePlayer(Entity player, float speed, float delta, IntPredicate isPressed) {
        movementManager.moveByDirection(player, gatherDirection(isPressed), speed, delta);
    }

    // Dash movement
    public void dashPlayer(Entity player, float speed, float delta, IntPredicate isPressed) {
        isDashing = true;
        dashTimer = DASH_DURATION;
        movementManager.moveByDirection(player, gatherDirection(isPressed), speed * DASH_MULTIPLIER, delta);
    }

    public void update(float delta) {
        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0f) isDashing = false;
        }
    }

    public boolean isDashing() { return isDashing; }

    private com.badlogic.gdx.math.Vector2 gatherDirection(IntPredicate isPressed) {
        com.badlogic.gdx.math.Vector2 direction = new com.badlogic.gdx.math.Vector2();
        if (isPressed.test(com.badlogic.gdx.Input.Keys.W) || isPressed.test(com.badlogic.gdx.Input.Keys.UP)) direction.y += 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.S) || isPressed.test(com.badlogic.gdx.Input.Keys.DOWN)) direction.y -= 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.A) || isPressed.test(com.badlogic.gdx.Input.Keys.LEFT)) direction.x -= 1f;
        if (isPressed.test(com.badlogic.gdx.Input.Keys.D) || isPressed.test(com.badlogic.gdx.Input.Keys.RIGHT)) direction.x += 1f;
        return direction;
    }
}
