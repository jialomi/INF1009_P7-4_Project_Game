package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.engine.entity.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

// /**
//  * OutputManager - handles all visuals
//  * hides the complex OpenGL calls (glClearing, Matrices)
//  * behind simple methods like beginFrame() and drawEntity()
//  */
public class OutputManager implements Disposable {

    // SpriteBatch is object that actually sends images to GPU
    private SpriteBatch batch;

    // camera determines where we are looking in game world
    private OrthographicCamera camera;

    // viewport handles how game looks on different screen sizes
    // such as adding black bars if aspect ratio doesnt match
    private Viewport viewport;

    // define a virtual resolution
    // game thinks screen is always 800x600, regardless of real monitor size
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    // state tracking
    private boolean initialised = false; // to prevent multiple init calls
    private boolean disposed = false; // to prevent multiple dispose calls

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("OutputManager is disposed and cannot be reinitialised");
        }
        if (initialised) {
            return; // already initialized, do nothing (idempotent)
        }

        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        // FITVIEWPORT - specific tool that prevents content from running off screen
        // scales entire game down to fit the window, maintaining aspect ratio
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // center camera so (0,0) isnt in corner, but middle
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        initialised = true;
    }

    /**
     * helper method to ensure OutputManager is initialized before use
     * throws exception if not initialized, preventing null pointer errors later
     */
    private void ensureInitialised() {
        if (!initialised || batch == null || camera == null || viewport == null) {
            throw new IllegalStateException("OutputManager must be initialized before use.");
        }
    }

    /**
     * called when window is resized by user
     * updates viewport so game doesnt look stretched or squashed
     */
    public void resize(int width, int height) {
        // 'true' parameter moves camera to center of new size
        // without this, resizing window shifts view and hides text
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    /**
     * converts screen coordinates (mouse) to world coordinates (game)
     * important as window size might be different from game size
     */
    public Vector2 getMouseInGameWorld() {
        // ensure OutputManager is initialized before trying to use camera/viewport
        ensureInitialised();

        // get raw mouse position from input
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();

        // ask camera to translate to world units
        // vector3 is required by unproject method
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0),
                viewport.getScreenX(), viewport.getScreenY(),
                viewport.getScreenWidth(), viewport.getScreenHeight());

        return new Vector2(worldPos.x, worldPos.y);
    }

    /**
     * call this at start of every render loop
     * clears screen to black and prepares batch for drawing
     */
    public void beginFrame() {
        // ensure OutputManager is initialized before trying to draw
        ensureInitialised();

        // clear screen to black (adds "black bars" if window aspect ratio is wrong)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // apply the camera updates to batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    /**
     * this method accepts any object that extends Entity
     * doesnt care if its a Player, Enemy, or Wall
     * as long as its an Entity, draws it
     */
    public void drawEntity(Entity e) {
        ensureInitialised(); // ensure OutputManager is initialized before trying to draw
        if (e.getTexture() != null) {
            batch.draw(e.getTexture(), e.getPosition().x, e.getPosition().y, e.getWidth(), e.getHeight());
        }
    }

    /**
     * call this at end of every render loop
     * sends final batch of images to GPU
     */
    public void endFrame() {
        if (batch.isDrawing())
            batch.end();
    }

    // helper getter if need to access batch for drawing text in GameMaster
    public SpriteBatch getBatch() {
        return batch;
    }

    public float getWorldWidth() {
        return WORLD_WIDTH;
    }

    public float getWorldHeight() {
        return WORLD_HEIGHT;
    }

    @Override
    public void dispose() {
        if (disposed) return; // already disposed, do nothing (idempotent)

        // SpriteBatch is heavy (uses GPU memory), so must dispose it manually
        if (batch != null)
            batch.dispose();

        batch = null;
        camera = null;
        viewport = null;
        disposed = true;
        initialised = false; // allow reinitialization if needed
    }
}