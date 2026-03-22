package io.github.some_example_name.engine.entity;

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

  public String getName() {
    return name;
  }
}
