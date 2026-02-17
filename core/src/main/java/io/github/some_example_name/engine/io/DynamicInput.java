package io.github.some_example_name.engine.io;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

/**
 * DynamicInput - input buffer
 * implements InputProcessor so can listen to hardware events
 * implements Disposable for cleanup consistency
 */
public class DynamicInput implements InputProcessor, Disposable {

    // maps to store state of any key without giant array
    // key = KeyCode (integer), value = IsPressed (boolean)
    private Map<Integer, Boolean> keyState;
    private Map<Integer, Boolean> keyJustPressed;

    // removed
    // private Vector2 mousePosition;

    public void initialize() {
        keyState = new HashMap<>();
        keyJustPressed = new HashMap<>();

        // removed
        // mousePosition = new Vector2();
    }

    /**
     * checks if a key is currently held down
     */
    public boolean isKeyPressed(int keycode) {
        return keyState.getOrDefault(keycode, false);
    }

    /**
     * checks if a key was pressed this frame
     * good to prevent unwanted repeated actions instantly
     */
    public boolean isKeyJustPressed(int keycode) {
        boolean pressed = keyJustPressed.getOrDefault(keycode, false);
        if (pressed) {
            // consume press so it returns false next time asked
            keyJustPressed.put(keycode, false);
        }
        return pressed;
    }

    public Vector2 getMousePosition() {
        // returning mousePosition directly might introduce a bug
        // where the mousePosition returned is not converted
        // from actual pixels to in game world pixels
        // return mousePosition;

        // fetch real position from engine, not raw pixel
        return IOManager.getInstance().getOutputManager().getMouseInGameWorld();
    }

    // libgdx hardware callbacks (os calls these)
    // these methods run whenever user touches keyboard/mouse

    @Override
    public boolean keyDown(int keycode) {
        keyState.put(keycode, true); // remember key is held down
        keyJustPressed.put(keycode, true); // mark key as just pressed
        return true; // return true to say handled this event
    }

    @Override
    public boolean keyUp(int keycode) {
        keyState.put(keycode, false); // key is let go
        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        // dont need to store x & y anymore
        // because getMousePosition() asks Gdx.input directly
        // mousePosition.set(x, y); // update mouse coordinates on click
        return true;
    }

    // track movement even if not clicking
    @Override
    public boolean mouseMoved(int x, int y) {
        // dont need to store x & y anymore
        // because getMousePosition() asks Gdx.input directly
        // mousePosition.set(x, y);
        return true;
    }

    // unused methods required by interface
    // leave these empty because dont need them yet
    @Override
    public boolean touchUp(int x, int y, int p, int b) {
        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchCancelled(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int p) {
        return false;
    }

    @Override
    public boolean scrolled(float x, float y) {
        return false;
    }

    @Override
    public void dispose() {
        keyState.clear();
        keyJustPressed.clear();
    }
}