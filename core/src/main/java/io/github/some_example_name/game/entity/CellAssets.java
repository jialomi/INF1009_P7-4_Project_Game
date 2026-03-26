package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.some_example_name.engine.io.AssetService;

final class CellAssets {
    private static AssetService assets;
    private static Animation<TextureRegion> cancerWalkAnimation;
    private static TextureRegion normalCellRegion;
    private static Animation<TextureRegion> tCellWalkAnimation;

    private CellAssets() {
    }

    static void initialize(AssetService assetService) {
        if (assetService == null) {
            throw new IllegalArgumentException("AssetService cannot be null.");
        }
        assets = assetService;
    }

    static Animation<TextureRegion> getCancerWalkAnimation() {
        ensureInitialised();
        if (cancerWalkAnimation == null) {
            Texture cancerSheet = loadTexture("cancer_cell.png");
            TextureRegion[][] split = TextureRegion.split(cancerSheet, cancerSheet.getWidth() / 4, cancerSheet.getHeight());
            TextureRegion[] frames = new TextureRegion[4];
            for (int i = 0; i < 4; i++) {
                frames[i] = split[0][i];
            }
            cancerWalkAnimation = new Animation<>(0.15f, frames);
            cancerWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
        return cancerWalkAnimation;
    }

    static TextureRegion getNormalCellTexture() {
        ensureInitialised();
        if (normalCellRegion == null) {
            Texture normalCellTexture = loadTexture("Normal_cell.png");
            normalCellRegion = new TextureRegion(normalCellTexture);
        }
        return normalCellRegion;
    }

    static Animation<TextureRegion> getTCellWalkAnimation() {
        ensureInitialised();
        if (tCellWalkAnimation == null) {
            Texture tCellSheet = loadTexture("tcell_strip.png");
            TextureRegion[] frames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                frames[i] = new TextureRegion(tCellSheet, i * 64, 0, 64, 64);
            }
            tCellWalkAnimation = new Animation<>(0.12f, frames);
            tCellWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
        return tCellWalkAnimation;
    }

    static void dispose() {
        cancerWalkAnimation = null;
        normalCellRegion = null;
        tCellWalkAnimation = null;
    }

    private static void ensureInitialised() {
        if (assets == null) {
            throw new IllegalStateException("CellAssets must be initialized with AssetService first.");
        }
    }

    private static Texture loadTexture(String assetPath) {
        if (!assets.isLoaded(assetPath, Texture.class)) {
            assets.loadTextureNow(assetPath);
        }
        return assets.getTexture(assetPath);
    }
}
