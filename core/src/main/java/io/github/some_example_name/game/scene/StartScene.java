package io.github.some_example_name.game.scene;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.game.util.SceneFlow;

public class StartScene extends AbstractScene {

    private final SceneManager sceneManager;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;

    public StartScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null) throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialise() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);
        titleFont.setColor(new Color(0.8f, 0.2f, 0.9f, 1f));

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.2f);
        bodyFont.setColor(Color.LIGHT_GRAY);
    }

    @Override
    protected void onUpdate(float delta) {
        if (getServices().getInput().isKeyJustPressed(Input.Keys.ENTER)) {
            SceneFlow.restartGame(sceneManager, getServices());
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth()  / 2f;
        float cy = output.getUiHeight() / 2f;

        drawCentered(output, titleFont, "TUMOUR CELL",                              cx, cy + 140f);
        drawCentered(output, titleFont, "SIMULATOR",                                cx, cy + 95f);
        drawCentered(output, bodyFont,  "- - - - - - - - - -",                      cx, cy + 55f);
        drawCentered(output, bodyFont,  "You are a cancer cell.",                   cx, cy + 20f);
        drawCentered(output, bodyFont,  "Infect the body.",                         cx, cy - 10f);
        drawCentered(output, bodyFont,  "Avoid the T-Cells.",                       cx, cy - 40f);
        drawCentered(output, bodyFont,  "Evolve. Spread. Survive.",                 cx, cy - 70f);
        drawCentered(output, bodyFont,  "- - - - - - - - - -",                      cx, cy - 105f);
        drawCentered(output, bodyFont,  "ENTER: BEGIN INFECTION",                   cx, cy - 135f);
        drawCentered(output, bodyFont,  "ARROW KEYS: Move  SHIFT: Dash  P: Pause", cx, cy - 165f);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, BitmapFont font, String text, float cx, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, cx - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (titleFont != null) titleFont.dispose();
        if (bodyFont  != null) bodyFont.dispose();
    }
}