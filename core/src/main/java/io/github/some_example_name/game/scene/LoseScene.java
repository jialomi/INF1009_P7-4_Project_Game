package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.CellInputMapper;
import io.github.some_example_name.game.util.RunStats;
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class LoseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;
    private String headerText;

    private Texture rTexture, escTexture, oTexture;

    // -- CHANGES HERE --
    // Variable to hold the lose thumbnail texture
    private Texture thumbnailTexture;

    // The rotating phrases for when the tumor is defeated
    private final String[] losePhrases = {
            "THE HOST SURVIVES.",
            "VITAL SYSTEMS STABALISE",
            "YOU CAN NO LONGER GROW."
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
        // -- CHANGES HERE --
        // Use a single font base, using internal scaling during render if needed.
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        // Changed font color to yellow for high visibility against the bright landscape.
        font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f)); 
        // Original: font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f)); // green for humanity winning

        // -- CHANGES HERE --
        // Load the lose thumbnail texture. Make sure to assume assets directory etc.
        thumbnailTexture = new Texture("images/scenes/losescene.jpg");

        // Key textures are loaded as before
        rTexture = getServices().getAssets().getTexture("key-gui/settingKeys/r.png");
        escTexture = getServices().getAssets().getTexture("key-gui/settingKeys/escape.png");
        oTexture = getServices().getAssets().getTexture("key-gui/settingKeys/o.png");

        // Select the random phrase.
        headerText = losePhrases[(int) (Math.random() * losePhrases.length)];
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

        float screenW = output.getUiWidth();
        float screenH = output.getUiHeight();
        
        // Draw the background at full brightness (Black text reads perfectly on bright backgrounds!)
        output.getBatch().setColor(Color.WHITE);
        output.getBatch().draw(thumbnailTexture, 0, 0, screenW, screenH);

        float cx = screenW / 2f;
        float cy = screenH / 2f;


        // WIDENED SPACING: Applied the same expanded layout from the WinScene
        float currentY = cy + 150f;
        float spacingMedium = 55f;
        float spacingSmall = 45f;
        float spacingPrompts = 55f;
        float spacingDonation = 50f;

        // ---------------------------------------------------------
        // 1. Header Text in Green
        // ---------------------------------------------------------
        font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f));
        drawCentered(output, headerText, cx, currentY);
        
        // ---------------------------------------------------------
        // 2. Stats and Separators in Black
        // ---------------------------------------------------------
        currentY -= spacingMedium; 
        font.setColor(Color.WHITE); // Switch font color to Black for everything else
        drawCentered(output, "- - - - - - - - - - - -", cx, currentY);
        
        currentY -= spacingMedium; 
        drawCentered(output, "CELLS EATEN: " + RunStats.getLastScore(), cx, currentY);
        
        currentY -= spacingSmall; 
        drawCentered(output, "TIME: " + String.format("%.1fs", RunStats.getLastSurvivalSeconds()), cx, currentY);
        
        currentY -= spacingSmall; 
        drawCentered(output, "BEST: " + RunStats.getBestScore(), cx, currentY);
        
        currentY -= spacingMedium; 
        drawCentered(output, "- - - - - - - - - - - -", cx, currentY);
        
        // ---------------------------------------------------------
        // 3. GUI Prompts in Black
        // ---------------------------------------------------------
        // The font color is already Black from the stats block above, 
        // so the prompt text will automatically be drawn in Black!
        float promptY = currentY - spacingPrompts;
        UIUtils.drawPromptCentered(output, font, rTexture, "[R] PLAY AGAIN", cx - 140f, promptY);
        UIUtils.drawPromptCentered(output, font, escTexture, "[ESC] MENU", cx + 140f, promptY);
        
        UIUtils.drawPromptCentered(output, font, oTexture, "[O] OPEN DONATION PAGE", cx, promptY - spacingDonation);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        // -- CHANGES HERE --
        // Dispose of the new thumbnail texture
        if (thumbnailTexture != null)
            thumbnailTexture.dispose();
            
        if (font != null)
            font.dispose();
    }
}
