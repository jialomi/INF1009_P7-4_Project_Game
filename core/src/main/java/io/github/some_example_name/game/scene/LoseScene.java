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

public class LoseScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;
    private String headerText;

    private PromptTextures prompts;
    private Texture thumbnailTexture;

    private final String[] losePhrases = {
            "THE HOST SURVIVES.",
            "VITAL SYSTEMS STABILISED",
            "YOU CAN NO LONGER GROW."
    };

    public LoseScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
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
        font.setColor(new Color(0.2f, 0.8f, 0.4f, 1f)); 
        thumbnailTexture = getServices().getAssets().getTexture("images/scenes/losescene.jpg");

        prompts = PromptTextures.load(getServices());
        headerText = losePhrases[(int) (Math.random() * losePhrases.length)];
    }

    @Override
    protected void onUpdate(float delta) {
        CellInputMapper mapper = ioController.getInputMapper();

        if (mapper.checkRestartAction()) {
            SceneFlow.restartGame(sceneManager, getServices(), ioController);
        } else if (mapper.checkMenuAction()) {
            SceneFlow.goToStart(sceneManager);
        } else if (mapper.checkDonateAction()) {
            ioController.getWebService().openDonationSiteInBrowser();
        }
    }

    @Override
    public void render(float delta, float interpolationAlpha) {
        OutputManager output = getServices().getOutputManager();
        output.beginFrame();
        output.beginUi();
        ResultSceneSupport.render(output, font, thumbnailTexture, Color.WHITE, headerText,
                new Color(0.2f, 0.8f, 0.4f, 1f), prompts);

        output.endUi();
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null)
            font.dispose();
    }
}
