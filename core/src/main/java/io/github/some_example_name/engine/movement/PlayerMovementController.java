package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;

public class PlayerMovementController {

    public void handleMovement(Entity entity, float speed, float deltaTime) {
        Vector2 movement = new Vector2(0, 0);
    
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.W) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.UP)) {
        movement.y += 1;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.S) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.DOWN)) {
        movement.y -= 1;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.A) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.LEFT)) {
        movement.x -= 1;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.D) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.RIGHT)) {
        movement.x += 1;
    }

    // Normalize diagonal movement
    if (movement.len() > 0) {
        movement.nor();
        Vector2 pos = entity.getPosition();
            entity.setPosition(
                pos.x + movement.x * speed * deltaTime,
                pos.y + movement.y * speed * deltaTime
            );
        }
    }
}