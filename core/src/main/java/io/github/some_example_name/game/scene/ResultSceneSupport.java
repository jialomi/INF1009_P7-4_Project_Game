package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.UIUtils;

final class ResultSceneSupport {
    private ResultSceneSupport() {
    }

    static void render(OutputManager output, BitmapFont font, Texture background, Color backgroundTint,
            String headerText, Color headerColor, PromptTextures controls) {
        // Win and lose scenes intentionally share the same stats/prompts layout
        // so only the outcome framing differs between them.
        float screenW = output.getUiWidth();
        float screenH = output.getUiHeight();
        float cx = screenW / 2f;
        float cy = screenH / 2f;

        SceneUiSupport.drawFullscreenBackground(output, background, screenW, screenH, backgroundTint);

        float currentY = cy + 150f;
        float spacingMedium = 55f;
        float spacingSmall = 45f;
        float spacingPrompts = 55f;
        float spacingDonation = 50f;

        font.setColor(headerColor);
        SceneUiSupport.drawCentered(output, font, headerText, cx, currentY);

        currentY -= spacingMedium;
        font.setColor(Color.WHITE);
        SceneUiSupport.drawDivider(output, font, cx, currentY);

        currentY -= spacingMedium;
        SceneUiSupport.drawCentered(output, font, "SCORE: " + RunStats.getLastScore(), cx, currentY);

        currentY -= spacingSmall;
        SceneUiSupport.drawCentered(output, font, "CELLS INFECTED: " + RunStats.getLastInfectedCells(), cx, currentY);

        currentY -= spacingSmall;
        SceneUiSupport.drawCentered(output, font,
                "FINAL SPREAD: " + Math.round(RunStats.getLastSpreadPercent()) + "%   LEVEL: " + RunStats.getLastLevel(),
                cx, currentY);

        currentY -= spacingMedium;
        SceneUiSupport.drawDivider(output, font, cx, currentY);

        currentY -= spacingSmall;
        SceneUiSupport.drawCentered(output, font,
                "TIME: " + String.format("%.1fs   BEST SCORE: %d", RunStats.getLastSurvivalSeconds(),
                        RunStats.getBestScore()),
                cx, currentY);

        font.setColor(Color.YELLOW);
        float promptY = currentY - spacingPrompts;
        UIUtils.drawPromptCentered(output, font, controls.r, "[R] PLAY AGAIN", cx - 140f, promptY);
        UIUtils.drawPromptCentered(output, font, controls.escape, "[ESC] MENU", cx + 140f, promptY);
        UIUtils.drawPromptCentered(output, font, controls.o, "[O] OPEN DONATION PAGE", cx, promptY - spacingDonation);
    }
}
