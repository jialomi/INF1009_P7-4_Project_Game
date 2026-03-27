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
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class StartScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    
    // -- CHANGES HERE --
    // We remove titleFont as the title is now inside the image artwork
    // We use smaller scales for bodyFont for description vs controls
    private BitmapFont mainFont;

    // -- CHANGES HERE --
    // Load the new thumbnail image
    private Texture thumbnailTexture;

    // Key textures are retained
    private Texture wTexture, aTexture, sTexture, dTexture;
    private Texture upTexture, leftTexture, downTexture, rightTexture;
    private Texture enterTexture;
    private Texture shiftTexture;
    private Texture pTexture;

    public StartScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null)
            throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
        this.ioController = ioController;
    }

    @Override
    protected void onInitialise() {
        // -- CHANGES HERE --
        // Switch to a single font base, using internal scaling during render if needed
        mainFont = new BitmapFont();
        mainFont.setColor(Color.LIGHT_GRAY);

        // -- CHANGES HERE --
        // Load your new thumbnail image. Replace path with where you saved it.
        thumbnailTexture = new Texture("images/scenes/startscene.jpg");

        // Key textures are loaded as before
        enterTexture = getServices().getAssets().getTexture("key-gui/settingKeys/enter.png");
        pTexture = getServices().getAssets().getTexture("key-gui/settingKeys/p.png");
        shiftTexture = getServices().getAssets().getTexture("key-gui/movement/shift.png");

        wTexture = getServices().getAssets().getTexture("key-gui/movement/w.png");
        aTexture = getServices().getAssets().getTexture("key-gui/movement/a.png");
        sTexture = getServices().getAssets().getTexture("key-gui/movement/s.png");
        dTexture = getServices().getAssets().getTexture("key-gui/movement/d.png");

        upTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_up.png");
        leftTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_left.png");
        downTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_down.png");
        rightTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_right.png");
    }

    @Override
    protected void onUpdate(float delta) {
        ioController.getAudioHandler().setMenuBGM();
        if (ioController.getInputMapper().checkConfirmAction()) {
            SceneFlow.goToHowTo(sceneManager);
        }
    }

   @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float screenW = output.getUiWidth();
        float screenH = output.getUiHeight();
        float cx = screenW / 2f;
        float cy = screenH / 2f;

        // ---------------------------------------------------------
        // 1. FULL SCREEN BACKGROUND
        // ---------------------------------------------------------
        output.getBatch().setColor(1f, 1f, 1f, 1f); 
        output.getBatch().draw(thumbnailTexture, 0, 0, screenW, screenH);
        output.getBatch().setColor(Color.WHITE); 

        // ---------------------------------------------------------
        // 2. CENTERED LORE TEXT (Right below the title)
        // ---------------------------------------------------------
        mainFont.getData().setScale(1.2f);
        mainFont.setColor(Color.WHITE);
        
        // SHIFTED UP: Increased from +20f to +130f to push it under the title
        float descTop = cy + 45f; 
        float lineSpace = 30f;

        drawCentered(output, mainFont, "You are a cancer cell.", cx, descTop);
        drawCentered(output, mainFont, "Infect the body. Avoid the T-Cells.", cx, descTop - lineSpace);
        drawCentered(output, mainFont, "Evolve. Spread. Survive.", cx, descTop - lineSpace * 2f);
// ---------------------------------------------------------
        // 3. ENTER PROMPT (Right below the paragraph)
        // ---------------------------------------------------------
        // INCREASED SCALE: Changed from 1.3f to 1.8f to make it much larger
        mainFont.getData().setScale(2.1f); 
        mainFont.setColor(Color.YELLOW); 
        
        // DYNAMIC POSITIONING: Increased the drop from -60f to -80f so the 
        // bigger text doesn't bump into the paragraph above it!
        float enterY = descTop - (lineSpace * 2f) - 200f; 
        UIUtils.drawPromptCentered(output, mainFont, enterTexture, "VIEW BRIEFING", cx, enterY);
        // ---------------------------------------------------------
        // 4. CONTROLS (Tucked into bottom corners)
        // ---------------------------------------------------------
        mainFont.getData().setScale(1.0f);
        mainFont.setColor(Color.LIGHT_GRAY);
        
        float iconSize = 40f;
        
        // ALIGNMENT GRID: Use these exact variables to keep left/right sides identical
        float keysBaseY = 30f;   // Base line for ASD, Down Arrows, and [P]
        float keysTopY = 75f;    // Base line for W, Up Arrow, and [SHIFT]
        float labelY = 140f;     // Base line for MOVEMENT/ACTIONS headers (clears the W key now)

        // --- Bottom Left: Movement ---
        float leftX = 40f;
        mainFont.draw(output.getBatch(), "MOVEMENT:", leftX, labelY);
        
        // Draw WASD and Arrows
        UIUtils.drawKeyCluster(output, wTexture, aTexture, sTexture, dTexture, leftX, keysBaseY, iconSize);
        UIUtils.drawKeyCluster(output, upTexture, leftTexture, downTexture, rightTexture, leftX + 160f, keysBaseY, iconSize);

        // --- Bottom Right: Actions ---
        float rightX = screenW - 250f;
        mainFont.draw(output.getBatch(), "ACTIONS:", rightX, labelY); 
        
        // Draw Dash (Shift) - Visually aligned with the 'W' and 'Up' keys
        output.getBatch().draw(shiftTexture, rightX, keysTopY, iconSize, iconSize);
        mainFont.draw(output.getBatch(), "[SHIFT] Dash", rightX + iconSize + 10f, keysTopY + 25f);
        
        // Draw Pause (P) - Visually aligned with the 'ASD' and 'Down' keys
        output.getBatch().draw(pTexture, rightX, keysBaseY, iconSize, iconSize);
        mainFont.draw(output.getBatch(), "[P] Pause", rightX + iconSize + 10f, keysBaseY + 25f);

        output.endUi();
        output.endFrame();
    }

    
    private void drawCentered(OutputManager output, BitmapFont font, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        // -- CHANGES HERE --
        // Important: Dispose of the new thumbnail texture
        if (thumbnailTexture != null)
            thumbnailTexture.dispose();
            
        // Disposing fonts as before (variable name updated)
        if (mainFont != null)
            mainFont.dispose();
    }
}
