package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.io.IOManager;

// === ISP FIX: Explicitly implements Collidable ===
public class TestEnemy extends RenderableEntity implements Collidable {

    // Textures (Stored here, but created by Factory)
    private TextureRegion redTexture;
    private TextureRegion yellowTexture;
    
    private float speed = 150f;
    private float soundTimer = 0f;
    private float slideDirection = 1f; 

    public TestEnemy(String name, float x, float y) {
        super(x, y, 48, 48);
        
        // === SRP FIX: Use Factory ===
        redTexture = DemoTextureFactory.createEnemyTexture(false);
        yellowTexture = DemoTextureFactory.createEnemyTexture(true);
        this.setTexture(redTexture);
    }

    @Override
    public void update(float deltaTime) {
        if (soundTimer > 0) soundTimer -= deltaTime;
        this.setTexture(redTexture); 

        float newY = getPositionY() - (speed * deltaTime);
        
        if (newY < -50) {
            newY = 650;
            float newX = (float) (Math.random() * 750);
            super.setPosition(newX, newY);
            slideDirection = Math.random() > 0.5 ? 1f : -1f;
        } else {
            super.setPosition(getPositionX(), newY);
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
                float dt = 0.016f; 
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