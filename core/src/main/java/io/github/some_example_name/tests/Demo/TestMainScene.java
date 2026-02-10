package io.github.some_example_name.tests.Demo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; 

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionManager;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.engine.scene.SceneManager; 

// === RENAMED CLASS ===
public class TestMainScene extends AbstractScene {
    
    private TestPlayer player;
    private List<RenderableEntity> localRenderables = new ArrayList<>();
    private CollisionManager collisionManager; 
    
    // UI Tools (HUD)
    private SpriteBatch uiBatch;
    private BitmapFont font;
    
    public TestMainScene() {
    }
    
    @Override
    protected void onInitialise() {
        // 1. Setup UI
        uiBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // 2. Setup Physics
        collisionManager = new CollisionManager();

        // 3. Create Objects
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
            collisionManager.addCollidable((Collidable)e);
        }
    }
    
    @Override
    protected void onUpdate(float delta) {
        // === PAUSE LOGIC ===
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Switch to the Pause Scene
            SceneManager.getInstance().setActive("pause");
            return; // Stop updating this frame
        }

        // Normal Game Updates
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
        output.beginFrame();
        for (RenderableEntity entity : localRenderables) {
            output.drawEntity(entity);
        }
        output.endFrame();
        
        // 2. Draw UI on top (HUD)
        uiBatch.begin();
        font.draw(uiBatch, "P FOR PAUSE", 20, Gdx.graphics.getHeight() - 20);
        uiBatch.end();
    }
    
    @Override
    protected void onDispose() {
        if (uiBatch != null) uiBatch.dispose();
        if (font != null) font.dispose();
        
        for (RenderableEntity e : localRenderables) {
            // Check specific types to dispose textures safely
            if (e instanceof TestWall) ((TestWall)e).dispose();
            if (e instanceof TestPlayer) ((TestPlayer)e).dispose();
            if (e instanceof TestEnemy) ((TestEnemy)e).dispose();
        }
    }
}