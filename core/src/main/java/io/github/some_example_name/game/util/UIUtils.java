package io.github.some_example_name.game.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.some_example_name.engine.io.OutputManager;

public final class UIUtils {

    // private constructor so nobody instantiates this utility class
    private UIUtils() {
    }

    /**
     * draws a key prompt icon alongside text, centered at given coordinates
     * automatically adjusts width for rectangular keys (like shift or enter)
     */
    public static void drawPromptCentered(OutputManager output, BitmapFont font, Texture icon, String text, float cx,
            float y) {
        float baseHeight = 44f;

        // auto width: read image's real proportions so wide keys arent squished
        float aspectRatio = (float) icon.getWidth() / (float) icon.getHeight();
        float drawWidth = baseHeight * aspectRatio;

        GlyphLayout layout = new GlyphLayout(font, text);
        float spacing = 12f;
        float totalWidth = drawWidth + spacing + layout.width;
        float startX = cx - (totalWidth / 2f);

        // draw image slightly below text baseline for vertical alignment
        output.getBatch().draw(icon, startX, y - (baseHeight * 0.75f), drawWidth, baseHeight);
        font.draw(output.getBatch(), layout, startX + drawWidth + spacing, y);
    }

    /**
     * draws a 4-key directional cluster (like wasd or arrows) in keyboard layout
     * startX and startY represents bottom-left corner of entire cluster
     */
    public static void drawKeyCluster(OutputManager output, Texture up, Texture left, Texture down, Texture right,
            float startX, float startY, float iconSize) {
        float gap = 4f; // 4 pixels of spacing between keys

        // top row (up key, centered over bottom 3)
        output.getBatch().draw(up, startX + iconSize + gap, startY + iconSize + gap, iconSize, iconSize);

        // bottom row (left, down, right)
        output.getBatch().draw(left, startX, startY, iconSize, iconSize);
        output.getBatch().draw(down, startX + iconSize + gap, startY, iconSize, iconSize);
        output.getBatch().draw(right, startX + (iconSize * 2f) + (gap * 2f), startY, iconSize, iconSize);
    }

    /**
     * draws key prompt icon alongside text starting at given startX coordinate
     * automatically adjusts width for rectangular keys (like shift or enter)
     */
    public static void drawPromptLeftAligned(OutputManager output, BitmapFont font, Texture icon, String text,
            float startX, float y) {
        float baseHeight = 44f;

        // auto width
        float aspectRatio = (float) icon.getWidth() / (float) icon.getHeight();
        float drawWidth = baseHeight * aspectRatio;

        float spacing = 12f;

        // draw image slightly below text baseline for vertical alignment
        output.getBatch().draw(icon, startX, y - (baseHeight * 0.75f), drawWidth, baseHeight);

        // draw text immediately after icon + spacing
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, startX + drawWidth + spacing, y);
    }
}
