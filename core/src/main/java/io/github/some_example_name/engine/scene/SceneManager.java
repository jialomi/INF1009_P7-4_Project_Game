package io.github.some_example_name.engine.scene;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.some_example_name.engine.util.Validation;

/**
 * Scene coordinator.
 * Handles registration, activation, lifecycle dispatch, and cleanup.
 */

public final class SceneManager {

    private final Map<String, EngineScreen> scenes;
    private final Set<EngineScreen> initialisedScenes;

    private EngineScreen active;
    private String activeName;
    private static final Runnable NO_OP = () -> {};
    private Runnable onSceneActivated = NO_OP;

    // For fixed-step updates, we would accumulate delta and run multiple updates if needed:
    private static final float FIXED_STEP = 1f / 60f;
    private static final int MAX_STEPS_PER_FRAME = 5;
    private float accumulator = 0f;

    public SceneManager() {
        this.scenes = new ConcurrentHashMap<>();
        this.initialisedScenes = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    public synchronized void load(String name, EngineScreen scene) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scene name cannot be null or blank.");
        }

        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null.");
        }
        EngineScreen previous = scenes.put(name, scene);

        // Nothing replaced or same instance re-registered
        if (previous == null || previous == scene) {
            return;
        }

        // If we replaced the currently active slot, keep scene state consistent
        if (previous == active && name.equals(activeName)) {
            initialiseIfNeeded(scene);
            active = scene;
            activeName = name;
        }

        // Dispose only if no other key still references old scene object
        if (!scenes.containsValue(previous)) {
            initialisedScenes.remove(previous);
            previous.dispose();
        }
    }
    
    public synchronized void unload(String name) {
        if (name == null || name.isBlank()) {
            return;
        }

        EngineScreen removed = scenes.remove(name);
        if (removed == null) {
            return;
        }

        if (removed == active) {
            active = null;
            activeName = null;
        }

        // Dispose only when this scene object is no longer referenced by any key
        if (!scenes.containsValue(removed)) {
            initialisedScenes.remove(removed);
            removed.dispose();
        }
    }

    public synchronized void setActive(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scene name cannot be null or blank.");
        }

        EngineScreen next = scenes.get(name);
        if (next == null) {
            throw new IllegalArgumentException("Scene not found: " + name);
        }

        initialiseIfNeeded(next);
        active = next;
        activeName = name;
        onSceneActivated.run(); // Notify listeners that a new scene is active
    }

    public synchronized void setOnSceneActivated(Runnable onSceneActivated) {
        this.onSceneActivated = (onSceneActivated != null) ? onSceneActivated : NO_OP;
    }

    public EngineScreen getActive() {
        return active;
    }

    public String getActiveName() {
        return activeName;
    }

    public void update(float delta) {
        Validation.requireValidDelta(delta);
        EngineScreen current = active;
        if (current != null) {
            current.update(delta);
        }
    }

    public void render(float delta) {
        Validation.requireValidDelta(delta);
        EngineScreen current = active;
        if (current != null) {
            current.render(delta);
        }
    }

    public void runFrame(float delta) {
        Validation.requireValidDelta(delta);

        float clamped = Math.min(delta, 0.25f); // Prevent huge delta from causing issues
        accumulator += clamped;

        int steps = 0;
        while (accumulator >= FIXED_STEP && steps < MAX_STEPS_PER_FRAME) {
            update(FIXED_STEP);
            accumulator -= FIXED_STEP;
            steps++;
        }

        render(clamped);
    }

    public void resize(int width, int height) {
        EngineScreen current = active;
        if (current != null) {
            current.resize(width, height);
        }
    }

    public synchronized void dispose() {
        Set<EngineScreen> uniqueScenes = Collections.newSetFromMap(new IdentityHashMap<>());
        uniqueScenes.addAll(scenes.values());

        for (EngineScreen scene : uniqueScenes) {
            scene.dispose();
        }

        scenes.clear();
        initialisedScenes.clear();
        active = null;
        activeName = null;
    }

    private void initialiseIfNeeded(EngineScreen scene) {
        if (!initialisedScenes.contains(scene)) {
            scene.initialise();
            initialisedScenes.add(scene);
        }
    }
}
