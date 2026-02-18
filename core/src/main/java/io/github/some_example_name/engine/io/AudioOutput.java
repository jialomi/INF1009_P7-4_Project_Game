package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

/**
 * AudioOutput - handles sound effects (sfx) and music
 * key feature: caching / lazy loading
 * loading audio from disk is slow
 * so only load once, store a map, and reuse next time
 */
public class AudioOutput implements Disposable {

    // cache to store loaded sounds so we dont reload them
    private Map<String, Sound> soundEffects;

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

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("AudioOutput is disposed and cannot be reinitialised");
        }
        if (initialised) return;    // already initialized, do nothing (idempotent)
        soundEffects = new HashMap<>();
        initialised = true;
    }

    private void ensureInitialised() {
        if (!initialised || soundEffects == null) {
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
        
        // check if this sound is already loaded
        if (!soundEffects.containsKey(fileName)) {
            // if no, check if file exists on disk
            if (Gdx.files.internal(fileName).exists()) {
                // load and save to map to cache it
                Sound s = Gdx.audio.newSound(Gdx.files.internal(fileName));
                soundEffects.put(fileName, s);
            } else {
                // print to console if audio file is missing
                System.err.println("[Audio] missing file: " + fileName);
                return;
            }
        }
        // play sound from cache
        soundEffects.get(fileName).play(sfxVolume);
    }

    // plays music continuously?
    public void playMusic(String fileName) {
        ensureInitialised(); // ensure AudioOutput is initialized before playing music
        if (fileName == null || fileName.isBlank()) return; // ignore invalid file names

        // check if requested song is already playing
        if (currentMusicPath.equals(fileName) && backgroundMusic != null && backgroundMusic.isPlaying()) {
            return; // do nothing & let it keep playing, without restarting
        }

        // stop any existing music (usually only want one bgm at a time)
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }

        // stream new music
        if (Gdx.files.internal(fileName).exists()) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            backgroundMusic.setLooping(true); // bgm usually loops
            backgroundMusic.setVolume(musicVolume); // usually quieter than SFX
            backgroundMusic.play();

            currentMusicPath = fileName; // remember what bgm is currently playing
        } else {
            // print to console if audio file is missing
            System.err.println("[Audio] missing file: " + fileName);
            return;
        }
    }

    public void preloadSound(String fileName) {
        ensureInitialised(); // ensure AudioOutput is initialized before preloading sound
        if (fileName == null || fileName.isBlank()) return; // ignore invalid file names
        if (soundEffects.containsKey(fileName)) return; // already loaded, do nothing

        if (Gdx.files.internal(fileName).exists()) {
            Sound s = Gdx.audio.newSound(Gdx.files.internal(fileName));
            soundEffects.put(fileName, s);
        } else {
            // print to console if audio file is missing
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

        // loop through every sound in cache and dispose it
        if (soundEffects != null) {
            for (Sound s : soundEffects.values()) {
                s.dispose();
            }
            soundEffects.clear();
        }
        // dispose music separately - its streamed, not cached usually
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        currentMusicPath = "";
        disposed = true;
        initialised = false; // allow reinitialization if needed
    }
}