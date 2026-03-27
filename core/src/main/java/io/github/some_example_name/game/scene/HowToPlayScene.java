package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.io.CellIOController;
import io.github.some_example_name.game.io.CellInputMapper;
import io.github.some_example_name.game.util.SceneFlow;
import io.github.some_example_name.game.util.UIUtils;

public class HowToPlayScene extends AbstractScene {
    private final SceneManager sceneManager;
    private final CellIOController ioController;

    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private Texture enterTexture;
    private Texture escTexture;
    private Texture shiftTexture;
    private Texture wTexture;
    private Texture aTexture;
    private Texture sTexture;
    private Texture dTexture;
    private Texture upTexture;
    private Texture leftTexture;
    private Texture downTexture;
    private Texture rightTexture;
    private TextureRegion tumorPreview;
    private TextureRegion healthyCellPreview;
    private TextureRegion tCellPreview;

    public HowToPlayScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null || ioController == null) {
            throw new IllegalArgumentException("SceneManager and CellIOController cannot be null");
        }
        this.sceneManager = sceneManager;
        this.ioController = ioController;
    }

    @Override
    protected void onInitialise() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(1.8f);
        titleFont.setColor(Color.WHITE);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.05f);
        bodyFont.setColor(Color.LIGHT_GRAY);

        enterTexture = getServices().getAssets().getTexture("key-gui/settingKeys/enter.png");
        escTexture = getServices().getAssets().getTexture("key-gui/settingKeys/escape.png");
        shiftTexture = getServices().getAssets().getTexture("key-gui/movement/shift.png");

        wTexture = getServices().getAssets().getTexture("key-gui/movement/w.png");
        aTexture = getServices().getAssets().getTexture("key-gui/movement/a.png");
        sTexture = getServices().getAssets().getTexture("key-gui/movement/s.png");
        dTexture = getServices().getAssets().getTexture("key-gui/movement/d.png");

        upTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_up.png");
        leftTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_left.png");
        downTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_down.png");
        rightTexture = getServices().getAssets().getTexture("key-gui/movement/arrow_right.png");

        Texture cancerSheet = getServices().getAssets().getTexture("images/cancer_cell.png");
        Texture normalCellTexture = getServices().getAssets().getTexture("images/Normal_cell.png");
        Texture tCellSheet = getServices().getAssets().getTexture("images/tcell_strip.png");

        tumorPreview = new TextureRegion(cancerSheet, 0, 0, cancerSheet.getWidth() / 4, cancerSheet.getHeight());
        healthyCellPreview = new TextureRegion(normalCellTexture);
        tCellPreview = new TextureRegion(tCellSheet, 0, 0, 64, 64);
    }

    @Override
    protected void onUpdate(float delta) {
        ioController.getAudioHandler().setMenuBGM();
        CellInputMapper mapper = ioController.getInputMapper();
        if (mapper.checkConfirmAction()) {
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

        float uiWidth = output.getUiWidth();
        float uiHeight = output.getUiHeight();
        float cx = uiWidth * 0.5f;

        SceneUiSupport.drawFullscreenBackground(output, getServices().getAssets().getTexture("images/bg.png"),
                uiWidth, uiHeight, new Color(0.07f, 0.05f, 0.1f, 1f));

        titleFont.draw(output.getBatch(), "HOW TO PLAY", cx - 135f, uiHeight - 60f);

        float leftX = 90f;
        float textX = 120f;
        float rightColumnX = uiWidth * 0.58f;
        float lineY = uiHeight - 125f;
        float lineGap = 42f;

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(output.getBatch(), "OBJECTIVE", leftX, lineY);
        bodyFont.setColor(Color.LIGHT_GRAY);
        bodyFont.draw(output.getBatch(), "Reach 100% spread before chemo and T-cells stop you.", textX, lineY - lineGap);
        bodyFont.draw(output.getBatch(), "Healthy cells increase spread, restore 5 HP, and grant EXP.", textX, lineY - lineGap * 2f);
        bodyFont.draw(output.getBatch(), "Chemo begins at 40% spread and removes 7% each hit.", textX, lineY - lineGap * 3f);
        bodyFont.draw(output.getBatch(), "Terminal stage begins at 90% spread. T-cells can no longer harm you there.",
                textX, lineY - lineGap * 4f);

        float spriteSectionTop = uiHeight - 125f;
        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(output.getBatch(), "KEY ACTORS", rightColumnX, spriteSectionTop);
        drawEntityGuide(output, rightColumnX + 18f, spriteSectionTop - 72f, tumorPreview, 62f,
                "Tumor Cell", "You control this cell. Infect tissue, grow stronger, and survive.");
        drawEntityGuide(output, rightColumnX + 18f, spriteSectionTop - 178f, healthyCellPreview, 44f,
                "Healthy Cell", "Consume these to gain spread, recover 5 HP, and earn EXP.");
        drawEntityGuide(output, rightColumnX + 18f, spriteSectionTop - 284f, tCellPreview, 54f,
                "T-Cell", "Immune hunter. Avoid contact until terminal stage shuts them down.");

        float controlsTop = uiHeight - 390f;
        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(output.getBatch(), "CONTROLS", leftX, controlsTop);

        float iconSize = 36f;
        UIUtils.drawKeyCluster(output, wTexture, aTexture, sTexture, dTexture, leftX, controlsTop - 120f, iconSize);
        UIUtils.drawKeyCluster(output, upTexture, leftTexture, downTexture, rightTexture, leftX + 165f,
                controlsTop - 120f, iconSize);

        bodyFont.setColor(Color.LIGHT_GRAY);
        bodyFont.draw(output.getBatch(), "Move the tumor cell", leftX + 325f, controlsTop - 68f);

        float shiftY = controlsTop - 170f;
        UIUtils.drawPromptLeftAligned(output, bodyFont, shiftTexture, "[SHIFT] Dash through danger", leftX, shiftY);

        float footerY = 92f;
        bodyFont.setColor(Color.YELLOW);
        UIUtils.drawPromptCentered(output, bodyFont, enterTexture, "START RUN", cx - 135f, footerY);
        UIUtils.drawPromptCentered(output, bodyFont, escTexture, "BACK TO TITLE", cx + 135f, footerY);

        output.endUi();
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (bodyFont != null) {
            bodyFont.dispose();
        }
    }

    private void drawEntityGuide(OutputManager output, float x, float y, TextureRegion sprite, float size,
            String title, String description) {
        float spriteBoxSize = 68f;
        float spriteX = x + (spriteBoxSize - size) * 0.5f;
        float spriteY = y - spriteBoxSize + (spriteBoxSize - size) * 0.5f + 10f;
        output.getBatch().draw(sprite, spriteX, spriteY, size, size);
        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(output.getBatch(), title, x + 84f, y);
        bodyFont.setColor(Color.LIGHT_GRAY);
        bodyFont.draw(output.getBatch(), description, x + 84f, y - 34f, 300f, Align.left, true);
    }
}
