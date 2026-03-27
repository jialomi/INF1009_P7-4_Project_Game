package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private BitmapFont mainFont;

    private Texture thumbnailTexture;

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
        mainFont = new BitmapFont();
        mainFont.setColor(Color.LIGHT_GRAY);
        thumbnailTexture = getServices().getAssets().getTexture("images/scenes/startscene.jpg");

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
        SceneUiSupport.drawFullscreenBackground(output, thumbnailTexture, screenW, screenH, Color.WHITE);

        // ---------------------------------------------------------
        // 2. CENTERED LORE TEXT (Right below the title)
        // ---------------------------------------------------------
        mainFont.getData().setScale(1.2f);
        mainFont.setColor(Color.WHITE);
        float descTop = cy + 45f; 
        float lineSpace = 30f;

        SceneUiSupport.drawCentered(output, mainFont, "You are a cancer cell.", cx, descTop);
        SceneUiSupport.drawCentered(output, mainFont, "Infect healthy cells. Endure chemo. Overwhelm the host.", cx,
                descTop - lineSpace);
        SceneUiSupport.drawCentered(output, mainFont, "Reach terminal spread and finish the infection.", cx,
                descTop - lineSpace * 2f);
        mainFont.getData().setScale(2.1f); 
        mainFont.setColor(Color.YELLOW); 
        float enterY = descTop - (lineSpace * 2f) - 200f; 
        UIUtils.drawPromptCentered(output, mainFont, enterTexture, "VIEW BRIEFING", cx, enterY);
        mainFont.getData().setScale(1.0f);
        mainFont.setColor(Color.LIGHT_GRAY);
        
        SceneUiSupport.drawMovementAndActionLegend(output, mainFont,
                wTexture, aTexture, sTexture, dTexture,
                upTexture, leftTexture, downTexture, rightTexture,
                shiftTexture, pTexture, "Pause", "[P]", screenW);

        output.endUi();
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (mainFont != null)
            mainFont.dispose();
    }
}
