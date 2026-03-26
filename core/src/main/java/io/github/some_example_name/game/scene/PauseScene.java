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
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class PauseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;

    private Texture pTexture, rTexture, escTexture;
    private Texture wTexture, aTexture, sTexture, dTexture;
    private Texture upTexture, leftTexture, downTexture, rightTexture;
    private Texture shiftTexture;

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
        font.getData().setScale(1.8f);
        font.setColor(Color.YELLOW);

        pTexture = getServices().getAssets().getTexture("key-gui/settingKeys/p.png");
        rTexture = getServices().getAssets().getTexture("key-gui/settingKeys/r.png");
        escTexture = getServices().getAssets().getTexture("key-gui/settingKeys/escape.png");

        wTexture = getServices().getAssets().getTexture("key-gui/movement/w.png");
        aTexture = getServices().getAssets().getTexture("key-gui/movement/a.png");
        sTexture = getServices().getAssets().getTexture("key-gui/movement/s.png");
        dTexture = getServices().getAssets().getTexture("key-gui/movement/d.png");

        upTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_up.png");
        leftTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_left.png");
        downTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_down.png");
        rightTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_right.png");

        shiftTexture = getServices().getAssets().getTexture("key-gui/movement/shift.png");
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

        float cx = output.getUiWidth() / 2f;
        float cy = output.getUiHeight() / 2f;

        drawCentered(output, "PAUSED", cx, cy + 100f);
        drawCentered(output, "- - - - - - - - - - - -", cx, cy + 60f);

        // UIUtils.drawPromptCentered(output, font, pTexture, "RESUME", cx, cy + 20f);
        // UIUtils.drawPromptCentered(output, font, rTexture, "RESTART", cx, cy - 25f);
        // UIUtils.drawPromptCentered(output, font, escTexture, "MAIN MENU", cx, cy -
        // 70f);

        // left aligned stack
        float leftColX = cx - 100f;
        UIUtils.drawPromptLeftAligned(output, font, pTexture, "[P] RESUME", leftColX, cy + 20f);
        UIUtils.drawPromptLeftAligned(output, font, rTexture, "[R] RESTART", leftColX, cy - 30f);
        UIUtils.drawPromptLeftAligned(output, font, escTexture, "[ESC] MAIN MENU", leftColX, cy - 80f);

        UIUtils.drawTextCentered(output, font, "- - - - - - - - - - - -", cx, cy - 110f);

        // movement cluster - pause scene
        float iconSize = 44f;
        float gap = 4f;
        float clusterWidth = (iconSize * 3f) + (gap * 2f);
        float clusterY = cy - 240f;
        float spacing = 20f;

        GlyphLayout slashLayout = new GlyphLayout(font, "/");
        GlyphLayout moveLayout = new GlyphLayout(font, "[WASD/ARROWS] Move");

        float totalWidth = clusterWidth + spacing + slashLayout.width + spacing + clusterWidth + spacing
                + moveLayout.width;
        float startX = cx - (totalWidth / 2f);
        float textY = clusterY + (iconSize * 1.25f);

        UIUtils.drawKeyCluster(output, wTexture, aTexture, sTexture, dTexture, startX, clusterY, iconSize);
        float currentX = startX + clusterWidth + spacing;

        font.draw(output.getBatch(), slashLayout, currentX, textY);
        currentX += slashLayout.width + spacing;

        UIUtils.drawKeyCluster(output, upTexture, leftTexture, downTexture, rightTexture, currentX, clusterY, iconSize);
        currentX += clusterWidth + spacing;

        font.draw(output.getBatch(), moveLayout, currentX, textY);

        UIUtils.drawPromptCentered(output, font, shiftTexture, "[SHIFT] Dash", cx, clusterY - 60f);

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
