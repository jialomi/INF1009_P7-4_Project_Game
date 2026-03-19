package io.github.some_example_name.game.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class CellDataManager {
    private final String saveFilePath = "saves/cell_run.json";
    private Json json;

    public CellDataManager() {
        this.json = new Json();
    }

    public Object loadOrganLayout(String organId) {
        // TODO: Return a parsed grid layout for the EntityManager to generate Walls
        System.out.println("Loading layout for organ: " + organId);
        return new Object();
    }

    public CancerStageConfig loadStageStats(int stage) {
        // Hardcoded balancing values as requested, could also be read from a JSON
        switch (stage) {
            case 1:
                return new CancerStageConfig(0.0f, false, false);
            case 2:
                return new CancerStageConfig(0.25f, false, false);
            case 3:
                return new CancerStageConfig(0.50f, true, false);
            case 4:
                return new CancerStageConfig(0.75f, true, true);
            default:
                return new CancerStageConfig(0.0f, false, false);
        }
    }

    public void saveGame(CellGameState state) {
        try {
            FileHandle file = Gdx.files.local(saveFilePath);
            file.writeString(json.prettyPrint(state), false);
            System.out.println("Game saved successfully to " + saveFilePath);
        } catch (Exception e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    public CellGameState loadGame() {
        try {
            FileHandle file = Gdx.files.local(saveFilePath);
            if (file.exists()) {
                return json.fromJson(CellGameState.class, file.readString());
            }
        } catch (Exception e) {
            System.err.println("Failed to load game, starting new run: " + e.getMessage());
        }
        return new CellGameState("Lungs", 1, 1, 0.0f); // Default new game state
    }
}