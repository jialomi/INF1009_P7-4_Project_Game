package io.github.some_example_name.engine.io;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * AudioOutput - handles sound effects (sfx) and music
 * Uses AssetService for asset lifecycle so sounds/music can be shared cleanly.
 */
public class AudioOutput implements Disposable {
    private final AssetService assets;
    private final boolean ownsAssets;

    private Music backgroundMusic;

    // default volumes for sfx and bgm
    // private float volume = 1.0f;
    private float sfxVolume = 1.0f;
    private float musicVolume = 0.5f;

    // to track bgm already playing
    private String currentMusicPath = "";

    // state tracking
    private boolean initialised = false;    // to prevent multiple init calls
    private boolean disposed = false;       // to prevent multiple dispose calls

    public AudioOutput() {
        this(new AssetService(), true);
    }

    public AudioOutput(AssetService assets) {
        this(assets, false);
    }

    private AudioOutput(AssetService assets, boolean ownsAssets) {
        if (assets == null) {
            throw new IllegalArgumentException("AssetService cannot be null.");
        }
        this.assets = assets;
        this.ownsAssets = ownsAssets;
    }

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("AudioOutput is disposed and cannot be reinitialised");
        }
        if (initialised) return;    // already initialized, do nothing (idempotent)
        if (ownsAssets) {
            assets.initialize();
        }
        initialised = true;
    }

    private void ensureInitialised() {
        if (!initialised) {
            throw new IllegalStateException("AudioOutput must be initialized before use.");
        }
    }

    /**
     * plays a sound effect
     * 
     * @param fileName path to file (e.g. "jump.wav")
     */
    public void playSound(String fileName) {
        ensureInitialised(); // ensure AudioOutput is initialized before playing sound
        if (fileName == null || fileName.isBlank()) return; // ignore invalid file names

        Sound sound = resolveSound(fileName);
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    // plays music continuously?
    public void playMusic(String fileName) {
        ensureInitialised(); // ensure AudioOutput is initialized before playing music
        if (fileName == null || fileName.isBlank()) return; // ignore invalid file names

        // check if requested song is already playing
        if (currentMusicPath.equals(fileName) && backgroundMusic != null && backgroundMusic.isPlaying()) {
            return; // do nothing & let it keep playing, without restarting
        }

        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        try {
            if (!assets.isLoaded(fileName, Music.class)) {
                assets.loadMusicNow(fileName);
            }
            backgroundMusic = assets.getMusic(fileName);
            backgroundMusic.setLooping(true); // bgm usually loops
            backgroundMusic.setVolume(musicVolume); // usually quieter than SFX
            backgroundMusic.play();

            currentMusicPath = fileName; // remember what bgm is currently playing
        } catch (IllegalArgumentException ex) {
            System.err.println("[Audio] missing file: " + fileName);
        }
    }

    public void preloadSound(String fileName) {
        ensureInitialised(); // ensure AudioOutput is initialized before preloading sound
        if (fileName == null || fileName.isBlank()) return; // ignore invalid file names
        try {
            if (!assets.isLoaded(fileName, Sound.class)) {
                assets.loadSoundNow(fileName);
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("[Audio] missing file: " + fileName);
        }
    }

    /**
     * sets volume for all future sound effects
     * does not change volume of sounds currently playing
     * 
     * @param volume 0.0f (mute) to 1.0f (max)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0f, Math.min(volume, 1f)); // clamp between 0 and 1
    }

    /**
     * sets volume for background music
     * updates volume of currently playing music immediately
     * 
     * @param volume 0.0f (mute) to 1.0f (max)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(volume, 1f)); // clamp between 0 and 1

        // apply change immediately if music is running
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(this.musicVolume);
        }
    }

    // getters in case of showing the current volume value on slider?
    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    @Override
    public void dispose() {
        if (disposed) return; // already disposed, do nothing (idempotent)

        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        currentMusicPath = "";
        if (ownsAssets) {
            assets.dispose();
        }
        disposed = true;
        initialised = false; // allow reinitialization if needed
    }

    public AssetService getAssets() {
        return assets;
    }

    private Sound resolveSound(String fileName) {
        try {
            if (!assets.isLoaded(fileName, Sound.class)) {
                assets.loadSoundNow(fileName);
            }
            return assets.getSound(fileName);
        } catch (IllegalArgumentException ex) {
            System.err.println("[Audio] missing file: " + fileName);
            return null;
        }
    }
}
