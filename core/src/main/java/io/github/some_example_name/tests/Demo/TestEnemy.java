package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.IOManager;
import io.github.some_example_name.engine.movement.MovementManager;

// === ISP FIX: Explicitly implements Collidable ===
public class TestEnemy extends RenderableEntity implements Collidable {

    // Textures (Stored here, but created by Factory)
    private TextureRegion redTexture;
    private TextureRegion yellowTexture;
    
    private float speed = 150f;
    private float soundTimer = 0f;
    private float slideDirection = 1f;
    private float lastDelta = 1f / 60f; // Default to 1/60th of a second for initial movement calculations

    private MovementManager movementManager;

    public TestEnemy(String name, float x, float y) {
        super(x, y, 48, 48);
        
        // === SRP FIX: Use Factory ===
        redTexture = DemoTextureFactory.createEnemyTexture(false);
        yellowTexture = DemoTextureFactory.createEnemyTexture(true);
        this.setTexture(redTexture);

        this.movementManager = new MovementManager();
    }

    @Override
    public void update(float deltaTime) {
        if (!Float.isNaN(deltaTime) && !Float.isInfinite(deltaTime) && deltaTime > 0f) {
            lastDelta = deltaTime;
        }
        if (soundTimer > 0) soundTimer -= deltaTime;
        this.setTexture(redTexture); 

        Vector2 velocity = new Vector2(0, -speed);
        movementManager.moveNpc(this, velocity, deltaTime);

        if (getPositionY() < -50) {
            float newX = (float) (Math.random() * 750);
            setPosition(newX, 650);
            slideDirection = Math.random() > 0.5 ? 1f : -1f;
        }
    }
    
    @Override
    public void onCollision(Collidable other) {
        if (other instanceof TestPlayer) {
            TestPlayer player = (TestPlayer) other;
            this.setTexture(yellowTexture);
            if (soundTimer <= 0) {
                IOManager.getInstance().getAudio().playSound("crash.mp3");
                soundTimer = 0.2f; 
            }
            if (this.getPositionY() > player.getPositionY() + player.getHeight()/2) {
                this.setPosition(this.getPositionX(), player.getPositionY() + player.getHeight());
            }
        }
        
        if (other instanceof TestWall) {
            TestWall wall = (TestWall) other;
            if (getPositionY() > wall.getPositionY()) {
                setPosition(getPositionX(), wall.getPositionY() + wall.getHeight());
                float slideSpeed = 100f; 
                float dt = Math.max(0f, Math.min(lastDelta, 1f / 30f)); // Cap delta time to 1/30th second (30 FPS)
                setPosition(getPositionX() + (slideSpeed * slideDirection * dt), getPositionY());
            }
        }
    }
    
    public void dispose() {
        // Textures managed by Factory creation, but we should dispose the underlying texture if we owned it.
        // For this demo, simple disposal is fine.
        if (redTexture != null) redTexture.getTexture().dispose();
        if (yellowTexture != null) yellowTexture.getTexture().dispose();
    }
}