package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.util.UIUtils;

final class SceneUiSupport {
    private SceneUiSupport() {
    }

    static void drawFullscreenBackground(OutputManager output, Texture texture, float width, float height, Color tint) {
        output.getBatch().setColor(tint);
        output.getBatch().draw(texture, 0f, 0f, width, height);
        output.getBatch().setColor(Color.WHITE);
    }

    static void drawCentered(OutputManager output, BitmapFont font, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    static void drawDivider(OutputManager output, BitmapFont font, float cx, float y) {
        drawCentered(output, font, "- - - - - - - - - - - -", cx, y);
    }

    static void ensureMenuMusic(CellIOController ioController) {
        // Menu scenes call this every frame; the audio layer already suppresses
        // restarting the same track, so this is an API to keep desired state.
        ioController.getAudioHandler().setMenuBGM();
    }

    static void drawMovementAndActionLegend(OutputManager output, BitmapFont font,
            MovementLegendTextures movement, Texture actionTexture, String actionLabel, String actionKeyLabel,
            float screenW) {
        float iconSize = 40f;
        float keysBaseY = 30f;
        float keysTopY = 75f;
        float labelY = 140f;

        float leftX = 40f;
        font.draw(output.getBatch(), "MOVEMENT:", leftX, labelY);
        UIUtils.drawKeyCluster(output, movement.w, movement.a, movement.s, movement.d, leftX, keysBaseY, iconSize);
        UIUtils.drawKeyCluster(output, movement.up, movement.left, movement.down, movement.right, leftX + 160f,
                keysBaseY, iconSize);

        float rightX = screenW - 250f;
        font.draw(output.getBatch(), "ACTIONS:", rightX, labelY);
        output.getBatch().draw(movement.shift, rightX, keysTopY, iconSize, iconSize);
        font.draw(output.getBatch(), "[SHIFT] Dash", rightX + iconSize + 10f, keysTopY + 25f);
        output.getBatch().draw(actionTexture, rightX, keysBaseY, iconSize, iconSize);
        font.draw(output.getBatch(), actionKeyLabel + " " + actionLabel, rightX + iconSize + 10f, keysBaseY + 25f);
    }
}
