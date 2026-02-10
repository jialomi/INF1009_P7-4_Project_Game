package io.github.some_example_name.engine.movement;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;

public class PlayerMovementController {
    
    /**
     * @param entity The player entity to move
     * @param speed Movement speed in pixels per second
     * @param deltaTime Time since last frame
     */

    public void handleWASDMovement(Entity entity, float speed, float deltaTime) {
    Vector2 pos = entity.getPosition();
    
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.W) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.UP)) {
        pos.y += speed * deltaTime;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.S) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.DOWN)) {
        pos.y -= speed * deltaTime;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.A) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.LEFT)) {
        pos.x -= speed * deltaTime;
    }
    if (IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.D) ||
        IOManager.getInstance().getDynamicInput().isKeyPressed(Input.Keys.RIGHT)) {
        pos.x += speed * deltaTime;
    }
    
    // NEW: Actually update the entity's position
    entity.setPosition(pos.x, pos.y);
}
    
    /**
     * Handle WASD with diagonal movement normalization
     * Prevents faster diagonal movement (moving up+right at same time)
     * 
     * @param entity The player entity to move
     * @param speed Movement speed in pixels per second
     * @param deltaTime Time since last frame
     */
    public void handleWASDMovementNormalized(Entity entity, float speed, float deltaTime) {
        Vector2 movement = new Vector2(0, 0);
        
        // Collect input
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
        
        // Normalize to prevent faster diagonal movement
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
