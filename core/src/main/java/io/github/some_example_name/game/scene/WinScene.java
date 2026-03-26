package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.CellInputMapper;
import io.github.some_example_name.game.io.WebIntegrationService;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class WinScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;
    private String headerText;

    private Texture rTexture, escTexture, oTexture;

    // The rotating phrases for when the tumor wins
    private final String[] winPhrases = {
            "The host fails.",
            "All systems collapse.",
            "There is nothing left to resist you."
    };

    public WinScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null)
            throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
        this.ioController = ioController;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(new Color(0.8f, 0.2f, 0.9f, 1f)); // purple for cancer win

        // Randomly select one phrase when the scene loads
        headerText = winPhrases[(int) (Math.random() * winPhrases.length)];

        rTexture = getServices().getAssets().getTexture("key-gui/settingKeys/r.png");
        escTexture = getServices().getAssets().getTexture("key-gui/settingKeys/escape.png");
        oTexture = getServices().getAssets().getTexture("key-gui/settingKeys/o.png");
    }

    @Override
    protected void onUpdate(float delta) {
        CellInputMapper mapper = ioController.getInputMapper();

        if (mapper.checkRestartAction()) {
            SceneFlow.restartGame(sceneManager, getServices(), ioController);
        } else if (mapper.checkMenuAction()) {
            SceneFlow.goToStart(sceneManager);
        } else if (mapper.checkDonateAction()) {
            ioController.getWebService().openDonationSiteInBrowser();
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth() / 2f;
        float cy = output.getUiHeight() / 2f;

        // Shifted the layout up slightly to center the new text block perfectly
        drawCentered(output, headerText, cx, cy + 110f);
        drawCentered(output, "- - - - - - - - - - - -", cx, cy + 50f);
        drawCentered(output, "CELLS EATEN: " + RunStats.getLastScore(), cx, cy + 10f);
        drawCentered(output, "TIME: " + String.format("%.1fs", RunStats.getLastSurvivalSeconds()), cx, cy - 25f);
        drawCentered(output, "BEST: " + RunStats.getBestScore(), cx, cy - 60f);
        drawCentered(output, "- - - - - - - - - - - -", cx, cy - 100f);

        UIUtils.drawPromptCentered(output, font, rTexture, "[R] PLAY AGAIN", cx - 130f, cy - 140f);
        UIUtils.drawPromptCentered(output, font, escTexture, "[ESC] MENU", cx + 130f, cy - 140f);
        UIUtils.drawPromptCentered(output, font, oTexture, "[O] OPEN DONATION PAGE", cx, cy - 185f);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (font != null)
            font.dispose();
    }
}
