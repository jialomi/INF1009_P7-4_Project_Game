package io.github.some_example_name.tests.Demo;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.movement.MovementManager;

// === ISP FIX: Explicitly implements Collidable ===
public class TestPlayer extends RenderableEntity implements Collidable {

    private MovementManager movementManager;

    public TestPlayer(String name, float x, float y) {
        super(x, y, 64, 64);
        
        // === SRP FIX: Use Factory ===
        this.setTexture(DemoTextureFactory.createPlayerTexture());
        
        movementManager = new MovementManager();
    }

    @Override
    public void update(float deltaTime) {
        movementManager.handlePlayerMovement(this, 400f, deltaTime);
    }
    
    @Override
    public void onCollision(Collidable other) {
        if (other instanceof TestEnemy) resolveEnemyCollision((TestEnemy) other);
        if (other instanceof TestWall) resolveWallCollision((TestWall) other);
    }
    
    private void resolveWallCollision(TestWall wall) {
        // (Logic remains identical - omitted for brevity)
        float pCX = getPositionX() + getWidth()/2;
        float pCY = getPositionY() + getHeight()/2;
        float wCX = wall.getPositionX() + wall.getWidth()/2;
        float wCY = wall.getPositionY() + wall.getHeight()/2;
        float dx = wCX - pCX;
        float dy = wCY - pCY;
        float combinedHalfWidth = (getWidth() + wall.getWidth()) / 2;
        float combinedHalfHeight = (getHeight() + wall.getHeight()) / 2;
        float overlapX = combinedHalfWidth - Math.abs(dx);
        float overlapY = combinedHalfHeight - Math.abs(dy);
        
        if (overlapX < overlapY) {
            if (dx > 0) setPosition(wall.getPositionX() - getWidth(), getPositionY());
            else setPosition(wall.getPositionX() + wall.getWidth(), getPositionY());
        } else {
            if (dy > 0) setPosition(getPositionX(), wall.getPositionY() - getHeight());
            else setPosition(getPositionX(), wall.getPositionY() + wall.getHeight());
        }
    }

    private void resolveEnemyCollision(TestEnemy enemy) {
        float pCX = getPositionX() + getWidth()/2;
        float pCY = getPositionY() + getHeight()/2;
        float eCX = enemy.getPositionX() + enemy.getWidth()/2;
        float eCY = enemy.getPositionY() + enemy.getHeight()/2;
        float dx = eCX - pCX;
        float dy = eCY - pCY;
        float overlapX = ((getWidth() + enemy.getWidth()) / 2) - Math.abs(dx);
        float overlapY = ((getHeight() + enemy.getHeight()) / 2) - Math.abs(dy);

        if (overlapX < overlapY) {
            if (dx > 0) enemy.setPosition(getPositionX() + getWidth(), enemy.getPositionY());
            else enemy.setPosition(getPositionX() - enemy.getWidth(), enemy.getPositionY());
        } else {
            if (dy < 0) setPosition(getPositionX(), enemy.getPositionY() + enemy.getHeight());
        }
    }

    public void dispose() {
        if (getTexture() != null) getTexture().getTexture().dispose();
    }
}