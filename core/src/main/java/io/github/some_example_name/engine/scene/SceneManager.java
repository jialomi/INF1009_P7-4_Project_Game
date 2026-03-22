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
    public static final float DEFAULT_FIXED_STEP_SECONDS = 1f / 60f;
    public static final int DEFAULT_MAX_STEPS_PER_FRAME = 5;

    private final Map<String, EngineScreen> scenes;
    private final Set<EngineScreen> initialisedScenes;
    private final float fixedStepSeconds;
    private final int maxStepsPerFrame;

    private EngineScreen active;
    private String activeName;
    private static final Runnable NO_OP = () -> {};
    private Runnable onSceneActivated = NO_OP;

    private float accumulator = 0f;
    private float interpolationAlpha = 1f;

    public SceneManager() {
        this(DEFAULT_FIXED_STEP_SECONDS, DEFAULT_MAX_STEPS_PER_FRAME);
    }

    public SceneManager(float fixedStepSeconds, int maxStepsPerFrame) {
        if (Float.isNaN(fixedStepSeconds) || Float.isInfinite(fixedStepSeconds) || fixedStepSeconds <= 0f) {
            throw new IllegalArgumentException("fixedStepSeconds must be finite and > 0.");
        }
        if (maxStepsPerFrame < 1) {
            throw new IllegalArgumentException("maxStepsPerFrame must be >= 1.");
        }
        this.scenes = new ConcurrentHashMap<>();
        this.initialisedScenes = Collections.newSetFromMap(new IdentityHashMap<>());
        this.fixedStepSeconds = fixedStepSeconds;
        this.maxStepsPerFrame = maxStepsPerFrame;
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

    public void render(float delta, float interpolationAlpha) {
        Validation.requireValidDelta(delta);
        EngineScreen current = active;
        if (current != null) {
            current.render(delta, interpolationAlpha);
        }
    }

    public void runFrame(float delta) {
        Validation.requireValidDelta(delta);

        float clamped = Math.min(delta, 0.25f); // Prevent huge delta from causing issues
        accumulator += clamped;

        int steps = 0;
        while (accumulator >= fixedStepSeconds && steps < maxStepsPerFrame) {
            update(fixedStepSeconds);
            accumulator -= fixedStepSeconds;
            steps++;
        }

        interpolationAlpha = Math.max(0f, Math.min(1f, accumulator / fixedStepSeconds));
        render(clamped, interpolationAlpha);
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

    public float getInterpolationAlpha() {
        return interpolationAlpha;
    }

    public float getFixedStepSeconds() {
        return fixedStepSeconds;
    }

    public int getMaxStepsPerFrame() {
        return maxStepsPerFrame;
    }
}
