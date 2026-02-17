package io.github.some_example_name.tests.Demo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
// removed: import com.badlogic.gdx.graphics.g2d.SpriteBatch; (dont need a separate batch)

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionManager;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestMainScene extends AbstractScene {

    private TestPlayer player;
    private List<RenderableEntity> localRenderables = new ArrayList<>();
    private CollisionManager collisionManager;

    // ui tools (hud)
    // removed: private SpriteBatch uiBatch;
    private BitmapFont font;

    public TestMainScene() {
    }

    @Override
    protected void onInitialise() {
        // 1. setup ui
        // removed: uiBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // 2. setup physics
        collisionManager = new CollisionManager();

        // 3. create objects
        addEntity(new TestWall(200, 300, 64));
        addEntity(new TestWall(550, 400, 64));

        player = new TestPlayer("Hero", 400, 100);
        addEntity(player);

        addEntity(new TestEnemy("Drop 1", 100, 600));
        addEntity(new TestEnemy("Drop 2", 300, 750));
        addEntity(new TestEnemy("Drop 3", 600, 900));
    }

    private void addEntity(RenderableEntity e) {
        localRenderables.add(e);
        if (e instanceof Collidable) {
            collisionManager.addCollidable((Collidable) e);
        }
    }

    @Override
    protected void onUpdate(float delta) {
        // === PAUSE LOGIC ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // switch to the pause scene
            SceneManager.getInstance().setActive("pause");
            return; // stop updating this frame
        }

        // normal game updates
        for (RenderableEntity entity : localRenderables) {
            entity.update(delta);
        }
        collisionManager.update();

        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.R)) {
            player.setPosition(400, 100);
        }
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.SPACE)) {
            IOManager.getInstance().getAudio().playSound("test.mp3");
        }
    }

    @Override
    public void render(float delta) {
        // 1. Draw Game World
        OutputManager output = IOManager.getInstance().getOutputManager();

        // begin the frame (sets up the camera scaling)
        output.beginFrame();

        // draw game entities
        for (RenderableEntity entity : localRenderables) {
            output.drawEntity(entity);
        }

        // 2. draw UI (use the same batch, so it uses same scaling)
        // coordinates:
        // X = 20 (left side)
        // Y = 580 (top side)
        // use '580' because virtual world height is 600
        // do not use Gdx.graphics.getHeight() because that changes when resizing the
        // window
        font.draw(output.getBatch(), "P FOR PAUSE", 20, 580);

        // 4. end the frame
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        // removed: if (uiBatch != null) uiBatch.dispose();
        if (font != null)
            font.dispose();

        for (RenderableEntity e : localRenderables) {
            // check specific types to dispose textures safely
            if (e instanceof TestWall)
                ((TestWall) e).dispose();
            if (e instanceof TestPlayer)
                ((TestPlayer) e).dispose();
            if (e instanceof TestEnemy)
                ((TestEnemy) e).dispose();
        }
    }
}