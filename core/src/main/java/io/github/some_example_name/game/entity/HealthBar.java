package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HealthBar {
  
  private final GameEntity owner;
  private final float barWidth;
  private final float barHeight;
  private final float yOffset;

  public HealthBar(GameEntity owner, float barWidth, float barHeight, float yOffset) {
    this.owner = owner;
    this.barWidth = barWidth;
    this.barHeight = barHeight;
    this.yOffset = yOffset;
  }

  public void render(ShapeRenderer shapeRenderer) {
    float percentage = getPercentage();

    float x = owner.getPositionX();
    float y = owner.getPositionY() + owner.getHeight() + yOffset;

    // Background (red)
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.rect(x, y, barWidth, barHeight);

    // Foreground (green fill based on remaining HP)
    shapeRenderer.setColor(Color.GREEN);
    shapeRenderer.rect(x, y, barWidth * percentage, barHeight);
  }

  public float getPercentage() {
    if (owner.getMaxHp() <= 0) return 0;
    return Math.max(0, owner.getHp() / owner.getMaxHp());
  }

  public GameEntity getOwner() { return owner; }
}
