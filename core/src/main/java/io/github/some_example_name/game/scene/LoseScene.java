package io.github.some_example_name.game.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.DynamicInput;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.WebIntegrationService; // <-- Added Import
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.SceneFlow;

public class LoseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private BitmapFont font;
    private String headerText;

    // The rotating phrases for when the tumor is defeated
    private final String[] losePhrases = {
        "The host survives.",
        "Vital systems stabilize.",
        "You can no longer grow."
    };

    public LoseScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null) throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f)); // green for humanity winning
        
        // Randomly select one phrase when the scene loads
        headerText = losePhrases[(int)(Math.random() * losePhrases.length)];
    }

    @Override
    protected void onUpdate(float delta) {
        DynamicInput input = getServices().getInput();
        if (input.isKeyJustPressed(Input.Keys.R)) {
            SceneFlow.restartGame(sceneManager, getServices());
        } else if (input.isKeyJustPressed(Input.Keys.ENTER)) {
            SceneFlow.goToStart(sceneManager);
        } else if (input.isKeyJustPressed(Input.Keys.D)) {
            // Trigger the browser link!
            new WebIntegrationService().openDonationSiteInBrowser();
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth()  / 2f;
        float cy = output.getUiHeight() / 2f;

        drawCentered(output, headerText,                         cx, cy + 110f);
        drawCentered(output, "- - - - - - - - - -",              cx, cy + 50f);
        drawCentered(output, "CELLS EATEN: "  + RunStats.getLastScore(), cx, cy + 10f);
        drawCentered(output, "TIME: " + String.format("%.1fs", RunStats.getLastSurvivalSeconds()), cx, cy - 25f);
        drawCentered(output, "BEST: "   + RunStats.getBestScore(),       cx, cy - 60f);
        drawCentered(output, "- - - - - - - - - -",              cx, cy - 100f);
        drawCentered(output, "R: PLAY AGAIN   ENTER: MENU",      cx, cy - 140f);
        drawCentered(output, "D: DONATE TO CANCER RESEARCH",     cx, cy - 180f); // New line!

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();
    }
}