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
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class StartScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;

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
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);
        titleFont.setColor(new Color(0.8f, 0.2f, 0.9f, 1f));

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.2f);
        bodyFont.setColor(Color.LIGHT_GRAY);

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
        if (ioController.getInputMapper().checkConfirmAction()) {
            SceneFlow.restartGame(sceneManager, getServices(), ioController);
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth() / 2f;
        float cy = output.getUiHeight() / 2f;

        drawCentered(output, titleFont, "TUMOUR CELL", cx, cy + 140f);
        drawCentered(output, titleFont, "SIMULATOR", cx, cy + 95f);
        drawCentered(output, bodyFont, "- - - - - - - - - -", cx, cy + 55f);
        drawCentered(output, bodyFont, "You are a cancer cell.", cx, cy + 20f);
        drawCentered(output, bodyFont, "Infect the body.", cx, cy - 10f);
        drawCentered(output, bodyFont, "Avoid the T-Cells.", cx, cy - 40f);
        drawCentered(output, bodyFont, "Evolve. Spread. Survive.", cx, cy - 70f);
        drawCentered(output, bodyFont, "- - - - - - - - - -", cx, cy - 105f);

        UIUtils.drawPromptCentered(output, bodyFont, enterTexture, "[ENTER] BEGIN INFECTION", cx, cy - 135f);

        // movement clusters
        float iconSize = 44f;
        float gap = 4f;
        float clusterWidth = (iconSize * 3f) + (gap * 2f);
        float clusterY = cy - 270f;
        float spacing = 20f;

        GlyphLayout slashLayout = new GlyphLayout(bodyFont, "/");
        GlyphLayout moveLayout = new GlyphLayout(bodyFont, "[WASD/ARROWS] Move");

        float totalWidth = clusterWidth + spacing + slashLayout.width + spacing + clusterWidth + spacing
                + moveLayout.width;
        float startX = cx - (totalWidth / 2f);
        float textY = clusterY + (iconSize * 1.25f);

        // draw wasd
        UIUtils.drawKeyCluster(output, wTexture, aTexture, sTexture, dTexture, startX, clusterY, iconSize);
        float currentX = startX + clusterWidth + spacing;

        // draw "/"
        bodyFont.draw(output.getBatch(), slashLayout, currentX, textY);
        currentX += slashLayout.width + spacing;

        // draw arrows
        UIUtils.drawKeyCluster(output, upTexture, leftTexture, downTexture, rightTexture, currentX, clusterY, iconSize);
        currentX += clusterWidth + spacing;

        // draw "Move"
        bodyFont.draw(output.getBatch(), moveLayout, currentX, textY);

        // dash and pause
        UIUtils.drawPromptCentered(output, bodyFont, shiftTexture, "[SHIFT] Dash", cx - 140f, clusterY - 40f);
        UIUtils.drawPromptCentered(output, bodyFont, pTexture, "[P] Pause", cx + 140f, clusterY - 40f);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, BitmapFont font, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (titleFont != null)
            titleFont.dispose();
        if (bodyFont != null)
            bodyFont.dispose();
    }
}
