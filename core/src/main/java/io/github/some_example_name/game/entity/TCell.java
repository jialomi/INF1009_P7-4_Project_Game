package io.github.some_example_name.game.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;

public class TCell extends GameEntity {
  
  private final HealthBar healthBar;
  private final MovementManager movementManager;

  private static final float TCELL_SIZE = 56f;
  private static final float DAMAGE_TO_CANCER = 15f;
  private static final float ATTACK_COOLDOWN = 1.0f;

  private float attackCooldown = 0f;

  public TCell(float x, float y) {
    super(x, y, TCELL_SIZE);
    applySize(TCELL_SIZE);
    this.healthBar = new HealthBar(this, TCELL_SIZE, 5f, 4f);
    this.movementManager = new MovementManager();
  }

  @Override
  public void update(float deltaTime) {
    if (!isAlive()) {
      setActive(false);
      return;
    }
    if (attackCooldown > 0f) attackCooldown -= deltaTime;

    Vector2 velocity = new Vector2(0, -50f);
    movementManager.moveNpc(this, velocity, deltaTime);

    if (getPositionY() < -50f) {
      setPosition(getPositionX(), 650f);
    }
  }

  @Override
  public void onCollision(Collidable other) {
    if (other instanceof CancerCell) {
      CancerCell cancer = (CancerCell) other;
      // TCell always damages cancer cell on contact
      if (attackCooldown <= 0f) {
        cancer.takeDamage(DAMAGE_TO_CANCER);
        attackCooldown = ATTACK_COOLDOWN;
      }
      // Cancer can only eat TCell if big enough
      if (cancer.canEat(this)) {
        takeDamage(cancer.getDamage());
      }
    }
  }

  @Override
  public int getCollisionLayer() { return 1 << 2; }

  @Override
  public int getCollisionMask() { return 1 << 0; }

  @Override
  public Rectangle getBounds() { return super.getBounds(); }

  public HealthBar getHealthBar() { return healthBar; }
}
