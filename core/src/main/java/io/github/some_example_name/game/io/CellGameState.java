package io.github.some_example_name.game.io;

public class CellGameState {
    public String currentOrgan;
    public int cellSize;
    public int cancerStage;
    public float chemoTimer;

    // Default constructor for potential JSON serialization
    public CellGameState() {
    }

    public CellGameState(String currentOrgan, int cellSize, int cancerStage, float chemoTimer) {
        this.currentOrgan = currentOrgan;
        this.cellSize = cellSize;
        this.cancerStage = cancerStage;
        this.chemoTimer = chemoTimer;
    }
}