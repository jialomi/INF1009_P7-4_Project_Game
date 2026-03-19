package io.github.some_example_name.game.io;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import java.util.Locale;

public class CellUIRenderer {
    private BitmapFont font;

    public CellUIRenderer() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void drawEntityHealthBar(Entity entity) {
        // placeholder: can use ShapeRenderer or small Texture to draw a bar over
        // entity.getPosition()
    }

    public void drawPlayerExpBar(int currentExp, int requiredExp) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        float top = output.getWorldHeight() - 20f;
        font.draw(output.getBatch(), "EXP: " + currentExp + " / " + requiredExp, 20, top);
    }

    public void drawSpreadPercentage(float spread) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        float top = output.getWorldHeight() - 40f;
        font.draw(output.getBatch(), "Spread: " + String.format(Locale.US, "%.1f%%", spread * 100), 20, top);
    }

    public void drawCancerStage(int stage) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        float top = output.getWorldHeight() - 60f;
        font.draw(output.getBatch(), "Stage: " + stage, 20, top);
    }

    public void drawChemoTimer(float timeRemaining) {
        if (timeRemaining > 0) {
            OutputManager output = IOManager.getInstance().getOutputManager();
            float top = output.getWorldHeight() - 80f;
            font.setColor(Color.RED);
            font.draw(output.getBatch(), "Chemo Reduction: " + String.format(Locale.US, "%.1fs", timeRemaining), 20,
                    top);
            font.setColor(Color.WHITE); // reset color
        }
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}