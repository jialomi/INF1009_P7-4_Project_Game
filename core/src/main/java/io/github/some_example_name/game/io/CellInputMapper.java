package io.github.some_example_name.game.io;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import java.util.UUID;

import io.github.some_example_name.engine.io.DynamicInput;
import io.github.some_example_name.engine.io.IOManager;

public class CellInputMapper {
    private UUID activeCellId;

    public void setActiveCellId(UUID id) {
        this.activeCellId = id;
    }

    public UUID getActiveCellId() {
        return activeCellId;
    }

    public Vector2 processMovementInput() {
        Vector2 movement = new Vector2(0, 0);
        DynamicInput input = IOManager.getInstance().getDynamicInput();

        if (input == null)
            return movement;

        if (input.isKeyPressed(Input.Keys.W) || input.isKeyPressed(Input.Keys.UP)) {
            movement.y += 1;
        }
        if (input.isKeyPressed(Input.Keys.S) || input.isKeyPressed(Input.Keys.DOWN)) {
            movement.y -= 1;
        }
        if (input.isKeyPressed(Input.Keys.A) || input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x -= 1;
        }
        if (input.isKeyPressed(Input.Keys.D) || input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x += 1;
        }

        // Normalize to prevent faster diagonal movement
        if (movement.len() > 0) {
            movement.nor();
        }
        return movement;
    }

    public boolean checkSplitAction() {
        return IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.SPACE);
    }

    public boolean checkSwapAction() {
        return IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.TAB);
    }

    public boolean checkPauseAction() {
        DynamicInput input = IOManager.getInstance().getDynamicInput();
        return input.isKeyJustPressed(Input.Keys.P) || input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    public Vector2 getMouseSelection() {
        // Relies on OutputManager to unproject the coordinates behind the scenes
        return IOManager.getInstance().getDynamicInput().getMousePosition();
    }
}