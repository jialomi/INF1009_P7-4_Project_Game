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
    private OutputManager outputManager; // reference to output manager for mouse position conversion

    // state tracking
    private boolean initialised = false; // to prevent multiple init calls
    private boolean disposed = false; // to prevent multiple dispose calls

    // removed
    // private Vector2 mousePosition;

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("DynamicInput is disposed and cannot be reinitialised");
        }
        if (initialised) {
            return; // already initialized, do nothing (idempotent)
        }
        keyState = new HashMap<>();
        keyJustPressed = new HashMap<>();

        // removed
        // mousePosition = new Vector2();
        initialised = true;
    }

    /**
     * helper method to ensure DynamicInput is initialized before use
     * throws exception if not initialized, preventing null pointer errors later
     */
    private void ensureInitialised() {
        if (!initialised || keyState == null || keyJustPressed == null) {
            throw new IllegalStateException("DynamicInput must be initialized before use.");
        }
    }

    public void setOutputManager(OutputManager outputManager) {
        this.outputManager = outputManager;
    }

    /**
     * checks if a key is currently held down
     */
    public boolean isKeyPressed(int keycode) {
        ensureInitialised(); // ensure DynamicInput is initialized before checking key state
        return keyState.getOrDefault(keycode, false);
    }

    /**
     * checks if a key was pressed this frame
     * good to prevent unwanted repeated actions instantly
     */
    public boolean isKeyJustPressed(int keycode) {
        ensureInitialised(); // ensure DynamicInput is initialized before checking key state
        boolean pressed = keyJustPressed.getOrDefault(keycode, false);
        if (pressed) {
            // consume press so it returns false next time asked
            keyJustPressed.put(keycode, false);
        }
        return pressed;
    }

    public Vector2 getMousePosition() {
        if (outputManager == null) return new Vector2();
        return outputManager.getMouseInGameWorld();
    }

    // libgdx hardware callbacks (os calls these)
    // these methods run whenever user touches keyboard/mouse

    @Override
    public boolean keyDown(int keycode) {
        ensureInitialised(); // ensure DynamicInput is initialized before handling input
        keyState.put(keycode, true); // remember key is held down
        keyJustPressed.put(keycode, true); // mark key as just pressed
        return true; // return true to say handled this event
    }

    @Override
    public boolean keyUp(int keycode) {
        ensureInitialised(); // ensure DynamicInput is initialized before handling input
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
        if (disposed) return; // already disposed, do nothing (idempotent)
        if (keyState != null) keyState.clear();
        if (keyJustPressed != null) keyJustPressed.clear();
        keyState = null;
        keyJustPressed = null;
        outputManager = null;
        disposed = true;
        initialised = false; // allow reinitialization if needed
    }
}