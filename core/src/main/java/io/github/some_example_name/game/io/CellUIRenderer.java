package io.github.some_example_name.game.io;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.OutputManager;
import java.util.Locale;

public class CellUIRenderer {
    private BitmapFont font;
    private final OutputManager output;

    public CellUIRenderer(OutputManager output) {
        if (output == null) {
            throw new IllegalArgumentException("OutputManager cannot be null");
        }
        this.output = output;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void drawEntityHealthBar(Entity entity) {
        // TODO:
        // placeholder: can use ShapeRenderer or small Texture to draw a bar over
        // entity.getPosition()
    }

    // THE COMMENTED OUT CODE BELOW IS A SAMPLE OF HOW THE ACTUAL
    // drawEntityHealthBar() METHOD WILL BE LIKE:

    // first - add this variable to top of CellUIRenderer class
    // private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer = new
    // com.badlogic.gdx.graphics.glutils.ShapeRenderer();

    // // then - replace empty method with this
    // public void drawEntityHealthBar(Entity entity) {
    // // 1 - check if entity is a GameEntity - meaning it has health
    // if (entity instanceof io.github.some_example_name.game.entity.GameEntity) {
    // io.github.some_example_name.game.entity.GameEntity gameEntity =
    // (io.github.some_example_name.game.entity.GameEntity) entity;

    // // 2 - calculate health percentage - e.g. 50 / 100 = 0.5
    // float healthPercent = gameEntity.getHp() / gameEntity.getMaxHp();

    // // 3 - find entity's position on screen
    // float x = gameEntity.getPositionX();
    // // add offset - like 64 pixels - so bar floats above the cell
    // float y = gameEntity.getPositionY() + 64;

    // float barWidth = 50;
    // float barHeight = 5;

    // // 4 - draw shapes
    // shapeRenderer.setProjectionMatrix(io.github.some_example_name.engine.io.IOManager.getInstance()
    // .getOutputManager().getCamera().combined);
    // shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);

    // // draw red background - missing health
    // shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
    // shapeRenderer.rect(x, y, barWidth, barHeight);

    // // draw green foreground - current health - over red bar
    // shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
    // shapeRenderer.rect(x, y, barWidth * healthPercent, barHeight);

    // shapeRenderer.end();
    // }
    // }

    public void drawPlayerExpBar(int currentExp, int requiredExp) {
        float top = output.getUiHeight() - 20f;
        font.draw(output.getBatch(), "EXP: " + currentExp + " / " + requiredExp, 20, top);
    }

    public void drawSpreadPercentage(float spread) {
        float top = output.getUiHeight() - 40f;
        font.draw(output.getBatch(), "Spread: " + String.format(Locale.US, "%.1f%%", spread * 100), 20, top);
    }

    public void drawCancerStage(int stage) {
        float top = output.getUiHeight() - 60f;
        font.draw(output.getBatch(), "Stage: " + stage, 20, top);
    }

    public void drawChemoTimer(float timeRemaining) {
        if (timeRemaining > 0) {
            float top = output.getUiHeight() - 80f;
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
