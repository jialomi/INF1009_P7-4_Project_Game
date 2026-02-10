package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // <--- NEW IMPORT for centering
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestStartScene extends AbstractScene {

    private SpriteBatch batch;
    private BitmapFont font;

    public TestStartScene() {
    }

    @Override
    protected void onInitialise() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
    }

    @Override
    protected void onUpdate(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            System.out.println(">> STARTING GAME...");
            SceneManager.getInstance().setActive("main");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        // === THE FIX ===
        // 1. Update the batch to match the CURRENT screen size (Fullscreen or Windowed)
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch.begin();
        
        String text = "PRESS ENTER TO START";
        
        // 2. Calculate PERFECT center (instead of guessing -300)
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float y = (Gdx.graphics.getHeight() + layout.height) / 2f;
        
        font.draw(batch, layout, x, y);
        
        batch.end();
    }

    @Override
    protected void onDispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}