package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TestEntity extends Entity {
  
  private String name;

  public TestEntity(String name, float x, float y) {
    super(x, y);
    this.name = name;
  }

  @Override
  public void update(float deltaTime) {
    applyMovement(deltaTime);
    System.out.printf("%s is at (%.2f, %.2f)%n", name, getPositionX(), getPositionY());
  }

  // ===== THE FIX: Added these missing methods =====
  @Override
  public TextureRegion getTexture() { return null; }

  @Override
  public float getWidth() { return 0; }

  @Override
  public float getHeight() { return 0; }
  // ==============================================

  public String getName() {
    return name;
  }
}