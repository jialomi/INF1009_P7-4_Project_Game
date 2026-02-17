package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager;

public class TestMainScene extends AbstractScene {

    private TestPlayer player;
    private BitmapFont font;

    @Override
    protected void onInitialise() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // Use AbstractScene API (Part 1 engine managers)
        createEntity(new TestWall(200, 300, 64));
        createEntity(new TestWall(550, 400, 64));

        player = new TestPlayer("Hero", 400, 100);
        createEntity(player);

        createEntity(new TestEnemy("Drop 1", 100, 600));
        createEntity(new TestEnemy("Drop 2", 300, 750));
        createEntity(new TestEnemy("Drop 3", 600, 900));
    }

    @Override
    protected void onUpdate(float delta) {
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.P)) {
            SceneManager.getInstance().setActive("pause");
            return;
        }

        // Entity update + collision update are already handled by AbstractScene.update()
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.R)) {
            player.setPosition(400, 100);
        }
        if (IOManager.getInstance().getDynamicInput().isKeyJustPressed(Input.Keys.SPACE)) {
            IOManager.getInstance().getAudio().playSound("test.mp3");
        }
    }

    @Override
    public void render(float delta) {
        OutputManager output = IOManager.getInstance().getOutputManager();
        output.beginFrame();

        for (Entity entity : getEntities()) {
            output.drawEntity(entity);
        }

        font.draw(output.getBatch(), "P FOR PAUSE", 20, 580);
        output.endFrame();
    }

    @Override
    protected void onDispose() {
        if (font != null) {
            font.dispose();
        }

        // Dispose demo entity textures
        for (Entity e : getEntities()) {
            if (e instanceof TestWall) ((TestWall) e).dispose();
            if (e instanceof TestPlayer) ((TestPlayer) e).dispose();
            if (e instanceof TestEnemy) ((TestEnemy) e).dispose();
        }
    }
}
