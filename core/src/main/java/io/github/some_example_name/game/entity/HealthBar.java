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

    // Perfectly center the health bar based on current growing size!
    float centerX = owner.getPositionX() + (owner.getWidth() / 2f);
    float x = centerX - (barWidth / 2f);
    float y = owner.getPositionY() + owner.getHeight() + yOffset;

    // Background (Dark gray so it doesn't look like damage)
    shapeRenderer.setColor(Color.DARK_GRAY);
    shapeRenderer.rect(x, y, barWidth, barHeight);

    // Foreground: Purple for Cancer, Orange for TCells, Green for Normal
    if (owner instanceof CancerCell) {
         shapeRenderer.setColor(new Color(0.8f, 0.2f, 0.8f, 1f)); 
    } else if (owner instanceof TCell) {
         shapeRenderer.setColor(Color.ORANGE);
    } else {
         shapeRenderer.setColor(Color.GREEN);
    }
    
    shapeRenderer.rect(x, y, barWidth * percentage, barHeight);
  }

  public float getPercentage() {
    if (owner.getMaxHp() <= 0) return 0;
    return Math.max(0, owner.getHp() / owner.getMaxHp());
  }

  public GameEntity getOwner() { return owner; }
}