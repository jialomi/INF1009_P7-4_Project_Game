package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestPauseScene extends AbstractScene {

    private SpriteBatch batch;
    private BitmapFont font;

    public TestPauseScene() {
    }

    @Override
    protected void onInitialise() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.0f);
        font.setColor(Color.YELLOW); 
    }

    @Override
    protected void onUpdate(float delta) {
        // Go back to Main Game on P
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Switch back to "main" (formerly game/demo)
            SceneManager.getInstance().setActive("main");
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen to Black
        ScreenUtils.clear(0, 0, 0, 1);
        
        batch.begin();
        
        // Draw PAUSE text
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        font.draw(batch, "PAUSED", centerX - 50, centerY + 50);
        
        // Draw Instruction
        font.getData().setScale(1.5f);
        font.draw(batch, "PRESS P TO UNPAUSE", centerX - 140, centerY - 50);
        font.getData().setScale(2.0f); // Reset scale
        
        batch.end();
    }

    @Override
    protected void onDispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}