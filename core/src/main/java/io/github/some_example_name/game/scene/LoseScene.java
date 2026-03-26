package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.CellInputMapper;
import io.github.some_example_name.game.io.WebIntegrationService;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.SceneFlow;

public class LoseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;
    private String headerText;

    // The rotating phrases for when the tumor is defeated
    private final String[] losePhrases = {
            "The host survives.",
            "Vital systems stabilize.",
            "You can no longer grow."
    };

    public LoseScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
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
        font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f)); // green for humanity winning

        // Randomly select one phrase when the scene loads
        headerText = losePhrases[(int) (Math.random() * losePhrases.length)];
    }

    @Override
    protected void onUpdate(float delta) {
        CellInputMapper mapper = ioController.getInputMapper();

        if (mapper.checkRestartAction()) {
            SceneFlow.restartGame(sceneManager, getServices(), ioController);
        } else if (mapper.checkConfirmAction()) {
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

        drawCentered(output, headerText, cx, cy + 110f);
        drawCentered(output, "- - - - - - - - - -", cx, cy + 50f);
        drawCentered(output, "CELLS EATEN: " + RunStats.getLastScore(), cx, cy + 10f);
        drawCentered(output, "TIME: " + String.format("%.1fs", RunStats.getLastSurvivalSeconds()), cx, cy - 25f);
        drawCentered(output, "BEST: " + RunStats.getBestScore(), cx, cy - 60f);
        drawCentered(output, "- - - - - - - - - -", cx, cy - 100f);
        drawCentered(output, "R: PLAY AGAIN   ENTER: MENU", cx, cy - 140f);
        drawCentered(output, "O: OPEN DONATION PAGE", cx, cy - 180f);

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
