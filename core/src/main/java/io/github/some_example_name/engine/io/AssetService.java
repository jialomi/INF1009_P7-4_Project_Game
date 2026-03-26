package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/**
 * Generic asset service for engine and game code.
 * Wraps libGDX AssetManager behind a small OOP-friendly API.
 */
public final class AssetService implements Disposable {
    private AssetManager assetManager;
    private boolean initialised;
    private boolean disposed;

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("AssetService is disposed and cannot be reinitialised.");
        }
        if (initialised) {
            return;
        }
        assetManager = new AssetManager();
        initialised = true;
    }

    public <T> void load(String assetPath, Class<T> type) {
        ensureInitialised();
        validateAssetRequest(assetPath, type);
        if (!assetManager.isLoaded(assetPath, type)) {
            assetManager.load(assetPath, type);
        }
    }

    public <T> void loadNow(String assetPath, Class<T> type) {
        load(assetPath, type);
        assetManager.finishLoadingAsset(assetPath);
    }

    public <T> boolean isLoaded(String assetPath, Class<T> type) {
        ensureInitialised();
        validateAssetRequest(assetPath, type);
        return assetManager.isLoaded(assetPath, type);
    }

    public boolean update() {
        ensureInitialised();
        return assetManager.update();
    }

    public void finishLoading() {
        ensureInitialised();
        assetManager.finishLoading();
    }

    public void finishLoading(String assetPath) {
        ensureInitialised();
        if (assetPath == null || assetPath.isBlank()) {
            throw new IllegalArgumentException("assetPath cannot be null or blank.");
        }
        assetManager.finishLoadingAsset(assetPath);
    }

    public <T> T get(String assetPath, Class<T> type) {
        ensureInitialised();
        validateAssetRequest(assetPath, type);
        if (!assetManager.isLoaded(assetPath, type)) {
            throw new IllegalStateException("Asset is not loaded: " + assetPath);
        }
        return assetManager.get(assetPath, type);
    }

    public void unload(String assetPath) {
        ensureInitialised();
        if (assetPath == null || assetPath.isBlank()) {
            return;
        }
        if (assetManager.isLoaded(assetPath)) {
            assetManager.unload(assetPath);
        }
    }

    public boolean exists(String assetPath) {
        if (assetPath == null || assetPath.isBlank()) {
            return false;
        }
        return Gdx.files.internal(assetPath).exists();
    }

    public void loadTexture(String assetPath) {
        load(assetPath, Texture.class);
    }

    public void loadTextureNow(String assetPath) {
        loadNow(assetPath, Texture.class);
    }

    public Texture getTexture(String assetPath) {
        return get(assetPath, Texture.class);
    }

    public void loadSound(String assetPath) {
        load(assetPath, Sound.class);
    }

    public void loadSoundNow(String assetPath) {
        loadNow(assetPath, Sound.class);
    }

    public Sound getSound(String assetPath) {
        return get(assetPath, Sound.class);
    }

    public void loadMusic(String assetPath) {
        load(assetPath, Music.class);
    }

    public void loadMusicNow(String assetPath) {
        loadNow(assetPath, Music.class);
    }

    public Music getMusic(String assetPath) {
        return get(assetPath, Music.class);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
        assetManager = null;
        disposed = true;
        initialised = false;
    }

    private void ensureInitialised() {
        if (!initialised || assetManager == null) {
            throw new IllegalStateException("AssetService must be initialized before use.");
        }
    }

    private <T> void validateAssetRequest(String assetPath, Class<T> type) {
        if (assetPath == null || assetPath.isBlank()) {
            throw new IllegalArgumentException("assetPath cannot be null or blank.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        if (!exists(assetPath)) {
            throw new IllegalArgumentException("Asset file does not exist: " + assetPath);
        }
    }
}
