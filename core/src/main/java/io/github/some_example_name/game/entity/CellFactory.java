package io.github.some_example_name.game.entity;

public class CellFactory {
  
  // Creates a player-controlled CancerCell at the given position
  public static CancerCell createCancerCell(float x, float y) {
    return new CancerCell(x, y);
  }

  // Creates an enemy NormalCell at the given position
  public static NormalCell createNormalCell(float x, float y) {
    return new NormalCell(x, y);
  }

  // Create an enemy TCell at the given position
  public static TCell createTCell(float x, float y) {
    return new TCell(x, y);
  }
}
