package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.CellInputMapper;
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class PauseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;

    private Texture bgTexture; 

    private PromptTextures prompts;
    private MovementLegendTextures movement;

    public PauseScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null)
            throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
        this.ioController = ioController;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        bgTexture = getServices().getAssets().getTexture("images/scenes/pausescene.jpg");
        prompts = PromptTextures.load(getServices());
        movement = MovementLegendTextures.load(getServices());
    }

    @Override
    protected void onUpdate(float delta) {
        CellInputMapper mapper = ioController.getInputMapper();
        
        if (mapper.checkPauseAction()) {
            sceneManager.setActive("game");
        } else if (mapper.checkRestartAction()) {
            SceneFlow.restartGame(sceneManager, getServices(), ioController);
        } else if (mapper.checkMenuAction()) {
            SceneFlow.goToStart(sceneManager);
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

        SceneUiSupport.drawFullscreenBackground(output, bgTexture, screenW, screenH, new Color(0.5f, 0.5f, 0.5f, 1f));

        font.getData().setScale(2.0f);
        font.setColor(Color.YELLOW);
        SceneUiSupport.drawCentered(output, font, "GAME PAUSED", cx, cy + 120f);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        SceneUiSupport.drawDivider(output, font, cx, cy + 70f);

        float menuStartX = cx - 110f; 
        UIUtils.drawPromptLeftAligned(output, font, prompts.p, "[P] RESUME", menuStartX, cy + 10f);
        UIUtils.drawPromptLeftAligned(output, font, prompts.r, "[R] RESTART", menuStartX, cy - 40f);
        UIUtils.drawPromptLeftAligned(output, font, prompts.escape, "[ESC] MAIN MENU", menuStartX, cy - 90f);
        
        SceneUiSupport.drawDivider(output, font, cx, cy - 130f);

        font.getData().setScale(1.0f);
        font.setColor(Color.LIGHT_GRAY);
        
        SceneUiSupport.drawMovementAndActionLegend(output, font, movement, prompts.p, "Resume", "[P]", screenW);

        output.endUi();
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();
    }
}
