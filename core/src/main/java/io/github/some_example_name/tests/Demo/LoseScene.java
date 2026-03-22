// core/src/main/java/io/github/some_example_name/tests/Demo/TestLoseScene.java
package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.some_example_name.engine.io.EngineServices;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;
import io.github.some_example_name.engine.io.DynamicInput;

public class LoseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private BitmapFont font;

    public LoseScene(SceneManager sceneManager, EngineServices services) {
        super(services);
        if (sceneManager == null) {
            throw new IllegalArgumentException("SceneManager cannot be null");
        }
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(Color.SCARLET);
    }

    @Override
    protected void onUpdate(float delta) {
        DynamicInput input = getServices().getInput();

        if (input.isKeyJustPressed(Input.Keys.R)) {
            SceneFlow.restartMainRun(sceneManager, getServices());
            return;
        } else if (input.isKeyJustPressed(Input.Keys.ENTER)) {
            SceneFlow.goToStart(sceneManager);
            return;
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();

        float cx = output.getUiWidth() / 2f;
        drawCentered(output, "GAME OVER", cx, output.getUiHeight() * 0.62f);
        drawCentered(output, "SCORE: " + RunStats.getLastScore(), cx, output.getUiHeight() * 0.53f);
        drawCentered(output, "SURVIVED: " + String.format("%.1fs", RunStats.getLastSurvivalSeconds()), cx, output.getUiHeight() * 0.47f);
        drawCentered(output, "BEST SCORE: " + RunStats.getBestScore(), cx, output.getUiHeight() * 0.41f);
        drawCentered(output, "R: RESTART   ENTER: START MENU", cx, output.getUiHeight() * 0.33f);

        output.endUi();
        output.endFrame();
    }

    private void drawCentered(OutputManager output, String text, float centerX, float y) {
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(output.getBatch(), layout, centerX - layout.width / 2f, y);
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();
    }
}
