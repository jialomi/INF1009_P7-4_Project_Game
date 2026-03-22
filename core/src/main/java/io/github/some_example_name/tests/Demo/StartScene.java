// core/src/main/java/io/github/some_example_name/tests/Demo/TestStartScene.java
package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class StartScene extends AbstractScene {

    private final SceneManager sceneManager;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;

    public StartScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null) {
            throw new IllegalArgumentException("SceneManager cannot be null");
        }
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialise() {
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);
        titleFont.setColor(Color.WHITE);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.2f);
        bodyFont.setColor(Color.LIGHT_GRAY);
    }

    @Override
    protected void onUpdate(float delta) {
        if (getServices().getInput().isKeyJustPressed(Input.Keys.ENTER)) {
            SceneFlow.restartMainRun(sceneManager, getServices());
            return;
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth() / 2f;
        drawCentered(output, titleFont, "ABSTRACT ENGINE DEMO", cx, output.getUiHeight() * 0.70f);
        drawCentered(output, bodyFont, "ENTER: START RUN", cx, output.getUiHeight() * 0.57f);
        drawCentered(output, bodyFont, "SURVIVE 45s TO WIN", cx, output.getUiHeight() * 0.51f);
        drawCentered(output, bodyFont, "P: PAUSE   RUNTIME HUD ENABLED", cx, output.getUiHeight() * 0.45f);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, BitmapFont font, String text, float centerX, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, centerX - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
    }
}
