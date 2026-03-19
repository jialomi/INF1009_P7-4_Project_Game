package io.github.some_example_name.game.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;

public class NormalCell extends GameEntity {
  
  private final HealthBar healthBar;
  private final MovementManager movementManager;

  private static final float NORMAL_SIZE = 32f;
  private static final float DAMAGE_TO_CANCER = 5f;

  public NormalCell(float x, float y) {
    super(x, y, NORMAL_SIZE);
    applySize(NORMAL_SIZE);
    this.healthBar = new HealthBar(this, NORMAL_SIZE, 5f, 4f);
    this.movementManager = new MovementManager();
  }

  @Override
  public void update(float deltaTime) {
    if(!isAlive()) {
      setActive(false);
      return;
    }
    Vector2 velocity = new Vector2(0, -30f);
    movementManager.moveNpc(this, velocity, deltaTime);

    if (getPositionY() < -50f) {
      setPosition(getPositionX(), 650f);
    }
  }

  @Override
  public void onCollision(Collidable other) {
    if (other instanceof CancerCell) {
      CancerCell cancer = (CancerCell) other;
      if (cancer.canEat(this)) {
        takeDamage(cancer.getDamage());
        cancer.takeDamage(DAMAGE_TO_CANCER);
      }
    }
  }

  @Override
  public int getCollisionLayer() { return 1 << 1; }

  @Override
  public int getCollisionMask() { return 1 << 0; }

  @Override
  public Rectangle getBounds() { return super.getBounds(); }

  public HealthBar getHealthBar() { return healthBar; }
}
