package io.github.some_example_name.game.io;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import java.util.UUID;

import io.github.some_example_name.engine.io.DynamicInput;

public class CellInputMapper {
    private UUID activeCellId;
    private final DynamicInput input;

    public CellInputMapper(DynamicInput input) {
        if (input == null) {
            throw new IllegalArgumentException("DynamicInput cannot be null");
        }
        this.input = input;
    }

    public void setActiveCellId(UUID id) {
        this.activeCellId = id;
    }

    public UUID getActiveCellId() {
        return activeCellId;
    }

    public Vector2 processMovementInput() {
        Vector2 movement = new Vector2(0, 0);

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

        // normalize to prevent faster diagonal movement
        if (movement.len() > 0) {
            movement.nor();
        }
        return movement;
    }

    public boolean checkSplitAction() {
        return input.isKeyJustPressed(Input.Keys.SPACE);
    }

    public boolean checkSwapAction() {
        return input.isKeyJustPressed(Input.Keys.TAB);
    }

    public boolean checkPauseAction() {
        return input.isKeyJustPressed(Input.Keys.P) || input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    public Vector2 getMouseSelection() {
        // relies on OutputManager to unproject the coordinates behind the scenes
        return input.getMousePosition();
    }
}
