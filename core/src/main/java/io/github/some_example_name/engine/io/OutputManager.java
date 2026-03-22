package io.github.some_example_name.engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.entity.Renderable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

// /**
//  * OutputManager - handles all visuals
//  * hides the complex OpenGL calls (glClearing, Matrices)
//  * behind simple methods like beginFrame() and drawEntity()
//  */
public class OutputManager implements Disposable {
    private final OutputConfiguration config;
    private SpriteBatch batch;
    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;
    private Viewport worldViewport;
    private Viewport uiViewport;

    private boolean initialised = false;
    private boolean disposed = false;
    private boolean frameActive = false;

    private boolean cameraBoundsEnabled = false;
    private float cameraMinX;
    private float cameraMinY;
    private float cameraMaxX;
    private float cameraMaxY;

    public OutputManager() {
        this(new OutputConfiguration());
    }

    public OutputManager(OutputConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("OutputConfiguration cannot be null.");
        }
        this.config = config;
        this.cameraMinX = config.getMinWorldWidth() * 0.5f;
        this.cameraMinY = config.getMinWorldHeight() * 0.5f;
        this.cameraMaxX = config.getMinWorldWidth() * 0.5f;
        this.cameraMaxY = config.getMinWorldHeight() * 0.5f;
    }

    public void initialize() {
        if (disposed) {
            throw new IllegalStateException("OutputManager is disposed and cannot be reinitialised");
        }
        if (initialised) {
            return; // already initialized, do nothing (idempotent)
        }

        batch = new SpriteBatch();
        worldCamera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();

        worldViewport = new ExtendViewport(config.getMinWorldWidth(), config.getMinWorldHeight(), worldCamera);
        uiViewport = new ScreenViewport(uiCamera);

        worldViewport.update((int) config.getMinWorldWidth(), (int) config.getMinWorldHeight(), true);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        resetCameraToViewportCenter();
        initialised = true;
    }

    /**
     * helper method to ensure OutputManager is initialized before use
     * throws exception if not initialized, preventing null pointer errors later
     */
    private void ensureInitialised() {
        if (!initialised || batch == null || worldCamera == null || uiCamera == null
                || worldViewport == null || uiViewport == null) {
            throw new IllegalStateException("OutputManager must be initialized before use.");
        }
    }

    /**
     * called when window is resized by user
     * updates viewport so game doesnt look stretched or squashed
     */
    public void resize(int width, int height) {
        if (worldViewport != null) {
            worldViewport.update(width, height, false);
        }
        if (uiViewport != null) {
            uiViewport.update(width, height, true);
        }
        if (worldCamera != null) {
            if (cameraBoundsEnabled) {
                clampWorldCamera();
            } else {
                resetCameraToViewportCenter();
            }
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

        Vector3 worldPos = worldCamera.unproject(new Vector3(screenX, screenY, 0),
                worldViewport.getScreenX(), worldViewport.getScreenY(),
                worldViewport.getScreenWidth(), worldViewport.getScreenHeight());

        return new Vector2(worldPos.x, worldPos.y);
    }

    public void beginFrame() {
        ensureInitialised();
        if (frameActive) {
            throw new IllegalStateException("Previous frame not ended. Call endFrame() before beginFrame().");
        }
        Gdx.gl.glClearColor(config.getClearRed(), config.getClearGreen(), config.getClearBlue(), config.getClearAlpha());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameActive = true;
    }

    public void beginWorld() {
        ensureFrameActive();
        applyWorldViewport();
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
    }

    public void endWorld() {
        if (batch.isDrawing()) {
            batch.end();
        }
    }

    public void beginUi() {
        ensureFrameActive();
        applyUiViewport();
        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
    }

    public void endUi() {
        if (batch.isDrawing()) {
            batch.end();
        }
    }

    public void drawEntity(Entity e, float interpolationAlpha) {
        ensureInitialised();
        if (e instanceof Renderable) {
            Renderable renderable = (Renderable) e;
            if (renderable.getTexture() != null) {
                batch.draw(renderable.getTexture(),
                    e.getInterpolatedPositionX(interpolationAlpha),
                    e.getInterpolatedPositionY(interpolationAlpha),
                    e.getWidth(),
                    e.getHeight());
            }
        }
    }

    public void endFrame() {
        if (batch.isDrawing()) {
            batch.end();
        }
        frameActive = false;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public float getWorldWidth() {
        return worldViewport != null ? worldViewport.getWorldWidth() : config.getMinWorldWidth();
    }

    public float getWorldHeight() {
        return worldViewport != null ? worldViewport.getWorldHeight() : config.getMinWorldHeight();
    }

    public float getUiWidth() {
        return uiViewport != null ? uiViewport.getWorldWidth() : Gdx.graphics.getWidth();
    }

    public float getUiHeight() {
        return uiViewport != null ? uiViewport.getWorldHeight() : Gdx.graphics.getHeight();
    }

    public void updateCamera(float targetX, float targetY) {
        ensureInitialised();
        worldCamera.position.set(targetX, targetY, 0f);
        if (cameraBoundsEnabled) {
            clampWorldCamera();
        }
    }

    public void setCameraBounds(float minX, float minY, float maxX, float maxY) {
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException("Camera bounds are invalid.");
        }
        cameraBoundsEnabled = true;
        cameraMinX = minX;
        cameraMinY = minY;
        cameraMaxX = maxX;
        cameraMaxY = maxY;
        clampWorldCamera();
    }

    public void clearCameraBounds() {
        cameraBoundsEnabled = false;
    }

    public Vector2 getCameraPosition() {
        ensureInitialised();
        return new Vector2(worldCamera.position.x, worldCamera.position.y);
    }

    @Override
    public void dispose() {
        if (disposed) return;
        if (batch != null) {
            batch.dispose();
        }

        batch = null;
        worldCamera = null;
        uiCamera = null;
        worldViewport = null;
        uiViewport = null;
        disposed = true;
        initialised = false;
        frameActive = false;
    }

    private void ensureFrameActive() {
        ensureInitialised();
        if (!frameActive) {
            throw new IllegalStateException("beginFrame() must be called before drawing.");
        }
        if (batch.isDrawing()) {
            throw new IllegalStateException("A drawing pass is already active.");
        }
    }

    private void applyWorldViewport() {
        worldViewport.apply();
    }

    private void applyUiViewport() {
        uiViewport.apply();
    }

    private void resetCameraToViewportCenter() {
        worldCamera.position.set(getWorldWidth() * 0.5f, getWorldHeight() * 0.5f, 0f);
        worldCamera.update();
    }

    private void clampWorldCamera() {
        float halfVisibleWidth = getWorldWidth() * 0.5f;
        float halfVisibleHeight = getWorldHeight() * 0.5f;

        float minCenterX = Math.min(cameraMinX + halfVisibleWidth, cameraMaxX - halfVisibleWidth);
        float maxCenterX = Math.max(cameraMinX + halfVisibleWidth, cameraMaxX - halfVisibleWidth);
        float minCenterY = Math.min(cameraMinY + halfVisibleHeight, cameraMaxY - halfVisibleHeight);
        float maxCenterY = Math.max(cameraMinY + halfVisibleHeight, cameraMaxY - halfVisibleHeight);

        worldCamera.position.x = clamp(worldCamera.position.x, minCenterX, maxCenterX);
        worldCamera.position.y = clamp(worldCamera.position.y, minCenterY, maxCenterY);
        worldCamera.update();
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
