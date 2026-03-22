package io.github.some_example_name.game.entity;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.io.DynamicInput;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.PlayerMovement;

public class CancerCell extends GameEntity {
  
  private final HealthBar healthBar;
  private final DynamicInput input;
  private final PlayerMovement playerMovement;

  private static final float STARTING_SIZE = 40f;
  private static final float EXP_PER_LEVEL = 100f;

  private float exp = 0f;
  private float expToNextLevel = EXP_PER_LEVEL;
  private int level = 1;

  public CancerCell(DynamicInput input, float x, float y) {
    super(x, y, STARTING_SIZE);
    if (input == null) {
      throw new IllegalArgumentException("DynamicInput cannot be null");
    }
    applySize(STARTING_SIZE);
    this.input = input;
    this.healthBar = new HealthBar(this, STARTING_SIZE, 5f, 4f);
    this.playerMovement = new PlayerMovement(new MovementManager());
    this.texture = TextureFactory.createPlayerTexture(); // Placehholder texture!
  }

  @Override
  public void update(float deltaTime) {
    if (!isAlive()) {
      setActive(false);
      return;
    }
    playerMovement.update(deltaTime);

    // normal move
    playerMovement.movePlayer(this, 200f, deltaTime,
        key -> input.isKeyPressed(key));

    // dash on shift
    if (input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT)) {
        playerMovement.dashPlayer(this, 200f, deltaTime,
            key -> input.isKeyPressed(key));
    }
    
    // Keep within boundary
    float x = Math.max(0, Math.min(getPositionX(), 800 - getWidth()));
    float y = Math.max(0, Math.min(getPositionY(), 600 - getHeight()));
    setPosition(x, y);
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

  public HealthBar getHealthBar()   { return healthBar; }
  public int getLevel()             { return level; }
  public float getExp()             { return exp; }
  public float getExpToNextLevel()  { return expToNextLevel; }
}
