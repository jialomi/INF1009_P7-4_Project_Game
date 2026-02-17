package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.util.Validation;
import java.util.function.IntPredicate;

public class PlayerMovementController {

    public void handleMovement(Entity entity, float speed, float deltaTime, IntPredicate isPressed) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
        if (isPressed == null) throw new IllegalArgumentException("Input sources cannot be null.");
        if (speed < 0f) throw new IllegalArgumentException("Speed cannot be negative.");
        Validation.requireValidDelta(deltaTime);

        Vector2 movement = new Vector2(0f, 0f);

        if (isPressed.test(Input.Keys.W) || isPressed.test(Input.Keys.UP)) movement.y += 1f;
        if (isPressed.test(Input.Keys.S) || isPressed.test(Input.Keys.DOWN)) movement.y -= 1f;
        if (isPressed.test(Input.Keys.A) || isPressed.test(Input.Keys.LEFT)) movement.x -= 1f;
        if (isPressed.test(Input.Keys.D) || isPressed.test(Input.Keys.RIGHT)) movement.x += 1f;

        // Normalize diagonal movement
        if (movement.len2() > 0f) {
            movement.nor();
            entity.setPosition(
                entity.getPositionX() + movement.x * speed * deltaTime,
                entity.getPositionY() + movement.y * speed * deltaTime
            );
        }
    }

    // Backward-compatible path
    public void handleMovement(Entity entity, float speed, float deltaTime) {
        handleMovement(entity, speed, deltaTime,
            key -> IOManager.getInstance().getDynamicInput().isKeyPressed(key));
    }
}