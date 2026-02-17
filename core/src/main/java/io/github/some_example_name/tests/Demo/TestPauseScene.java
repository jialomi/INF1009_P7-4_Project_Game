package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

// removed: SpriteBatch import (use the engine's batch)
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestPauseScene extends AbstractScene {

    // removed: private SpriteBatch batch;
    private BitmapFont font;

    public TestPauseScene() {
    }

    @Override
    protected void onInitialise() {
        // removed: batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        font.setColor(Color.YELLOW);
    }

    @Override
    protected void onUpdate(float delta) {
        // go back to main game on "p"
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.P)) {
            // switch back to "main" (formerly game/demo)
            SceneManager.getInstance().setActive("main");
        }
    }

    @Override
    public void render(float delta) {
        OutputManager output = IOManager.getInstance().getOutputManager();

        // 1. clear screen and setup 800x600 view
        output.beginFrame();

        // 2. define center of the virtual world
        float centerX = output.getWorldWidth() / 2f;
        float centerY = output.getWorldHeight() / 2f;

        // 3. draw pause Text
        String title = "PAUSED";
        GlyphLayout layoutTitle = new GlyphLayout(font, title);
        font.draw(output.getBatch(), layoutTitle, centerX - (layoutTitle.width / 2), centerY + 50);

        // 4. draw instruction text
        font.getData().setScale(1.5f);
        String sub = "PRESS P TO UNPAUSE";
        GlyphLayout layoutSub = new GlyphLayout(font, sub);
        font.draw(output.getBatch(), layoutSub, centerX - (layoutSub.width / 2), centerY - 50);

        font.getData().setScale(2.0f); // reset scale

        output.endFrame();
    }

    @Override
    protected void onDispose() {
        // removed: if (batch != null) batch.dispose();
        if (font != null)
            font.dispose();
    }
}