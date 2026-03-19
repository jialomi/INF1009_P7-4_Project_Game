package io.github.some_example_name.game.entity;

import io.github.some_example_name.engine.entity.RenderableEntity;
import io.github.some_example_name.engine.collision.Collidable;

public abstract class GameEntity extends RenderableEntity implements Collidable {
  
  private float hp;
  private float maxHp;
  private float size;
  private float damage;

  public GameEntity(float x, float y, float size) {
    super(x, y, size, size);
    this.size = size;
  }

  // Called when levelling up - resizes the entity and scales stats
  public void applySize(float newSize) {
    this.size = newSize;
    setSize(newSize, newSize);
    this.maxHp = newSize * 2f;
    this.hp = maxHp;
    this.damage = newSize * 0.5f;
  }

  public void takeDamage(float amount) {
    this.hp -= amount;
    if (this.hp < 0) this.hp = 0;
  }

  public boolean canEat(GameEntity other) {
    return this.size > other.size;
  }

  public boolean isAlive() {
    return this.hp > 0 && isActive();
  }

  // Getters
  public float getHp()      { return hp; }
  public float getMaxHp()   { return maxHp; }
  public float getSize()    { return size; }
  public float getDamage()  { return damage; }
  
  // Setters
  public void setHp(float hp)         { this.hp = hp; } 
  public void setMaxHp(float maxHp)   { this.maxHp = maxHp; }
  public void setDamage(float damage) { this.damage = damage; } 
}
