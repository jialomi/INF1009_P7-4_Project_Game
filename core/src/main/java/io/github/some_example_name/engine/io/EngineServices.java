package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public final class EngineServices implements Disposable {
    private final AssetService assets;
    private final AudioOutput audio;
    private final DynamicInput input;
    private final OutputManager output;
    private boolean initialised;
    private boolean disposed;

    public EngineServices() {
        this(new OutputConfiguration());
    }

    public EngineServices(OutputConfiguration outputConfiguration) {
        if (outputConfiguration == null) {
            throw new IllegalArgumentException("OutputConfiguration cannot be null.");
        }
        this.assets = new AssetService();
        this.audio = new AudioOutput(assets);
        this.input = new DynamicInput();
        this.output = new OutputManager(outputConfiguration);
    }

    public EngineServices(AudioOutput audio, DynamicInput input, OutputManager output) {
        this(audio != null ? audio.getAssets() : null, audio, input, output);
    }

    public EngineServices(AssetService assets, AudioOutput audio, DynamicInput input, OutputManager output) {
        if (assets == null || audio == null || input == null || output == null) {
            throw new IllegalArgumentException("Engine services cannot contain null components.");
        }
        if (audio.getAssets() != assets) {
            throw new IllegalArgumentException("AudioOutput must use the same AssetService instance.");
        }
        this.assets = assets;
        this.audio = audio;
        this.input = input;
        this.output = output;
    }

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("EngineServices is disposed and cannot be reinitialised.");
        }
        if (initialised) {
            return;
        }

        assets.initialize();
        audio.initialize();
        output.initialize();
        input.initialize();
        input.setOutputManager(output);
        Gdx.input.setInputProcessor(input);
        initialised = true;
    }

    public AssetService getAssets() {
        return assets;
    }

    public AudioOutput getAudio() {
        return audio;
    }

    public DynamicInput getInput() {
        return input;
    }

    public OutputManager getOutputManager() {
        return output;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        if (audio != null) {
            audio.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        if (output != null) {
            output.dispose();
        }
        if (input != null) {
            input.dispose();
        }
        Gdx.input.setInputProcessor(null);
        disposed = true;
        initialised = false;
    }
}
