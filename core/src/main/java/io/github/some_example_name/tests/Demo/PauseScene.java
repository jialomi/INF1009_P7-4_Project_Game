// core/src/main/java/io/github/some_example_name/tests/Demo/TestPauseScene.java
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

public class PauseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private BitmapFont font;

    public PauseScene(SceneManager sceneManager, EngineServices services) {
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
        font.setColor(Color.YELLOW);
    }

    @Override
    protected void onUpdate(float delta) {
        DynamicInput input = getServices().getInput();

        if (input.isKeyJustPressed(Input.Keys.P)) {
            sceneManager.setActive("main");
            return;
        } else if (input.isKeyJustPressed(Input.Keys.R)) {
            SceneFlow.restartMainRun(sceneManager, getServices());
            return;
        } else if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
        drawCentered(output, "PAUSED", cx, output.getUiHeight() * 0.60f);
        drawCentered(output, "P: RESUME", cx, output.getUiHeight() * 0.50f);
        drawCentered(output, "R: RESTART RUN", cx, output.getUiHeight() * 0.44f);
        drawCentered(output, "ESC: BACK TO START", cx, output.getUiHeight() * 0.38f);

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
