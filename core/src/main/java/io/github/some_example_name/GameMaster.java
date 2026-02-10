package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.scene.SceneManager;

// === NEW IMPORTS ===
import io.github.some_example_name.tests.Demo.TestStartScene;  // formerly Menu
import io.github.some_example_name.tests.Demo.TestMainScene;   // formerly Game
import io.github.some_example_name.tests.Demo.TestPauseScene;  // New!

public class GameMaster extends Game {
    
    private IOManager ioManager;
    private SceneManager sceneManager;
    
    @Override
    public void create() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      GAME ENGINE INITIALIZATION        ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        ioManager = IOManager.getInstance();
        ioManager.init(); 
        
        if (ioManager.getOutputManager() != null) {
            ioManager.getOutputManager().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        
        sceneManager = SceneManager.getInstance();
        
        // === LOAD SCENES WITH NEW NAMES ===
        sceneManager.load("start", new TestStartScene());
        sceneManager.load("main", new TestMainScene());
        sceneManager.load("pause", new TestPauseScene());
        
        // Start at the Menu
        sceneManager.setActive("start");
        
        System.out.println("✓ Engine Online: Start Scene Loaded");
    }
    
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        sceneManager.runFrame(delta);
    }
    
    @Override
    public void resize(int width, int height) {
        if (ioManager != null && ioManager.getOutputManager() != null) {
            ioManager.getOutputManager().resize(width, height);
        }
        if (sceneManager != null) {
            sceneManager.resize(width, height);
        }
    }
    
    @Override
    public void dispose() {
        if (sceneManager != null) sceneManager.dispose();
        if (ioManager != null) ioManager.dispose();
    }
}