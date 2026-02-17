package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

/**
 * IOManager - central hub for all hardware interaction
 * singleton pattern
 * only 1 manager handling inputs and outputs to avoid conflicts
 */
public class IOManager implements Disposable {

    // static variable to hold only 1 instance of this class
    private static IOManager instance;

    // encapsulation (composition)
    // private so no other class can mess with them directly
    // IOManager owns these sub managers
    private AudioOutput audio;
    private DynamicInput dynamicInput;
    private OutputManager outputManager;

    // state tracking
    private boolean initialised = false; // to prevent multiple init calls
    private boolean disposed = false; // to prevent multiple dispose calls

    /**
     * private constructor
     * prevents other classes from calling new IOManager()
     * instead, must use getInstance()
     */
    private IOManager() {
        audio = new AudioOutput();
        dynamicInput = new DynamicInput();
        outputManager = new OutputManager();
    }

    /**
     * public accessor
     * the only way to get IOManager
     * if it doesnt exist yet, creates it
     * if it exists, returns existing one
     */
    public static IOManager getInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }

    /**
     * init() - engine starter
     * call this inside GameMaster.create() to boot up system
     */
    public void init() {
        if (disposed) {
            throw new IllegalStateException("IOManager is disposed and cannot be reinitialised");
        }
        if (initialised) {
            return; // already initialized, do nothing (idempotent)
        }
        // initialize sub managers in specific order
        audio.initialize();
        outputManager.initialize();
        dynamicInput.initialize();
        dynamicInput.setOutputManager(outputManager); // give input manager access to output manager for mouse position conversion
        // connect input logic to libgdx hardware listener
        // without this line, keyboard/mouse clicks wont register
        Gdx.input.setInputProcessor(dynamicInput);

        initialised = true;
    }

    // getters
    // provide read only access to sub managers
    public AudioOutput getAudio() {
        return audio;
    }

    public DynamicInput getDynamicInput() {
        return dynamicInput;
    }

    public OutputManager getOutputManager() {
        return outputManager;
    }

    /**
     * dispose() - the cleanup crew
     * inherited from Disposable interface
     * must be called when game closes to free up RAM (or memory leak happens)
     */
    @Override
    public void dispose() {
        if (disposed) {
            return; // already disposed, do nothing (idempotent)
        }
        if (audio != null)
            audio.dispose();
        if (outputManager != null)
            outputManager.dispose();
        if (dynamicInput != null)
            dynamicInput.dispose();

        // disconnect input processor so libgdx stops sending events to a dead object
        Gdx.input.setInputProcessor(null);

        disposed = true;
        initialised = false; // allow reinitialization if needed
    }
}