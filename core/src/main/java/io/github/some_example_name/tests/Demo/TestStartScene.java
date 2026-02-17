package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // <--- NEW IMPORT for centering

// removed: SpriteBatch import (use the engine's batch)
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestStartScene extends AbstractScene {

    // removed: private SpriteBatch batch;
    private BitmapFont font;

    public TestStartScene() {
    }

    @Override
    protected void onInitialise() {
        // removed: batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
    }

    @Override
    protected void onUpdate(float delta) {
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.ENTER)) {
            System.out.println(">> STARTING GAME...");
            SceneManager.getInstance().setActive("main");
        }
    }

    @Override
    public void render(float delta) {
        // 1. get engine's OutputManager
        OutputManager output = IOManager.getInstance().getOutputManager();

        // 2. begin frame (this clears screen and sets up 800x600 camera)
        output.beginFrame();

        String text = "PRESS ENTER TO START";

        // 3. calculate center based on virtual resolution (800x600)
        // do not use Gdx.graphics.getWidth() because that is the window size
        GlyphLayout layout = new GlyphLayout(font, text);

        float worldWidth = output.getWorldWidth();
        float worldHeight = output.getWorldHeight();
        float x = (worldWidth - layout.width) / 2f;
        float y = (worldHeight + layout.height) / 2f;

        // 4. draw using engine's batch
        font.draw(output.getBatch(), layout, x, y);

        output.endFrame();
    }

    @Override
    protected void onDispose() {
        // removed: if (batch != null) batch.dispose();
        if (font != null)
            font.dispose();
    }
}