package io.github.some_example_name.engine.io;
// package main.java.io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.some_example_name.engine.entity.Entity;

public class OutputManager implements Disposable {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Virtual resolution (Game logic thinks the screen is this size)
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    public void initialize() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // FitViewport ensures the game looks good on any screen size (black bars if
        // needed)
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
    }

    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
    }

    public void beginFrame() {
        // Clear Screen Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    /**
     * Draws any object that inherits from Entity
     */
    public void drawEntity(Entity e) {
        if (e.getTexture() != null) {
            batch.draw(e.getTexture(),
                    e.getPosition().x,
                    e.getPosition().y,
                    e.getWidth(),
                    e.getHeight());
        }
    }

    public void endFrame() {
        if (batch.isDrawing()) {
            batch.end();
        }
    }

    @Override
    public void dispose() {
        if (batch != null)
            batch.dispose();
    }
}