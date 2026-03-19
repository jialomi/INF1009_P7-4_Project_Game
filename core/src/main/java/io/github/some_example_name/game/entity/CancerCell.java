package io.github.some_example_name.game.entity;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;

public class CancerCell extends GameEntity {
  
  private final HealthBar healthBar;
  private final MovementManager movementManager;

  private static final float STARTING_SIZE = 40f;
  private static final float EXP_PER_LEVEL = 100f;

  private float exp = 0f;
  private float expToNextLevel = EXP_PER_LEVEL;
  private int level = 1;

  public CancerCell(float x, float y) {
    super(x, y, STARTING_SIZE);
    applySize(STARTING_SIZE);
    this.healthBar = new HealthBar(this, STARTING_SIZE, 5f, 4f);
    this.movementManager = new MovementManager();
  }

  @Override
  public void update(float deltaTime) {
    if (!isAlive()) {
      setActive(false);
      return;
    }
    movementManager.handlePlayerMovement(this, 200f, deltaTime);
  }

  @Override
  public void onCollision(Collidable other) {
    if (other instanceof NormalCell) {
      NormalCell normal = (NormalCell) other;
      if (!normal.isAlive()) {
        gainExp(20f);
      }
    } else if (other instanceof TCell) {
      TCell tCell = (TCell) other;
      if (!tCell.isAlive()) {
        gainExp(50f);
      }
    }
  }

  public void gainExp(float amount) {
    this.exp += amount;
    if (this.exp >= expToNextLevel) {
      levelUp();
    }
  }

  private void levelUp() {
    level ++;
    exp = 0f;
    expToNextLevel *= 1.5f;
    float newSize = STARTING_SIZE + (level * 8f);
    applySize(newSize);
    healthBar.getOwner(); // reference kept alive
  }

  @Override
  public int getCollisionLayer() { return 1 << 0; }

  @Override
  public int getCollisionMask() { return (1 << 1) | (1 << 2); }

  @Override
  public Rectangle getBounds() { return super.getBounds(); }

  public HealthBar getHealthBar()   { return healthBar; }
  public int getLevel()             { return level; }
  public float getExp()             { return exp; }
  public float getExpToNextLevel()  { return expToNextLevel; }
}
