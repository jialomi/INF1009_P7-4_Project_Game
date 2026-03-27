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

public class WinScene extends AbstractScene {

    private final SceneManager sceneManager;
    private final CellIOController ioController;
    private BitmapFont font;
    private String headerText;

    private Texture bgTexture; 

    private PromptTextures prompts;

    private final String[] winPhrases = {
            "THE HOST FAILS",
            "ALL SYSTEMS COLLAPSE",
            "THERE IS NOTHING LEFT TO RESIST YOU."
    };

    public WinScene(SceneManager sceneManager, EngineServices services, CellIOController ioController) {
        super(services);
        if (sceneManager == null)
            throw new IllegalArgumentException("SceneManager cannot be null");
        this.sceneManager = sceneManager;
        this.ioController = ioController;
    }

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.getData().setScale(1.8f); // Slightly smaller to fit cleanly in the void
        font.setColor(new Color(0.9f, 0.7f, 1.0f, 1f)); // Light purple/pink to match the necrotic theme

        bgTexture = getServices().getAssets().getTexture("images/scenes/winscene.jpg");
        

        headerText = winPhrases[(int) (Math.random() * winPhrases.length)];

        prompts = PromptTextures.load(getServices());
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
        ResultSceneSupport.render(output, font, bgTexture, new Color(0.4f, 0.4f, 0.4f, 1f), headerText,
                new Color(0.9f, 0.4f, 1.0f, 1f), prompts);

        output.endUi();
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null) font.dispose();
    }
}
