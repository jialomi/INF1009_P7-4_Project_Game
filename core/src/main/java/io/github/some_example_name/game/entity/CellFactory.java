package io.github.some_example_name.game.entity;

import io.github.some_example_name.engine.io.AssetService;
import io.github.some_example_name.game.io.CellInputMapper;

public class CellFactory {
    public static void initializeSharedAssets(AssetService assets) {
        CellAssets.initialize(assets);
    }

    public static CancerCell createCancerCell(float x, float y, CellInputMapper inputMapper) {
        return new CancerCell(x, y, inputMapper);
    }

    public static NormalCell createNormalCell(float x, float y, float speed, float fleeRange) {
        return new NormalCell(x, y, speed, fleeRange);
    }

    public static TCell createTCell(float x, float y, float speed) {
        return new TCell(x, y, speed);
    }

    public static void disposeSharedAssets() {
        CellAssets.dispose();
    }
}
