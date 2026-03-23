package io.github.some_example_name.game.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class TextureFactory {
     private static TextureRegion cancerCell; //player
    private static TextureRegion enemyRed;
    private static TextureRegion enemyYellow;
    private static final Map<Integer, TextureRegion> wallBySize = new HashMap<>();
    private static final Map<String, TextureRegion> boundaryBySize = new HashMap<>();

    private TextureFactory() {}

    public static TextureRegion createPlayerTexture() {
    if (cancerCell == null) {
        Texture sheet = new Texture("cancer_cell.png");
        // TODO: replace 48, 48 with actual frame size from _SpriteSheetInfo.txt
        cancerCell = new TextureRegion(sheet, 64, 144, 32, 72);
    }
    return cancerCell;
}

    public static TextureRegion createEnemyTexture(boolean isHit) {
    if (enemyRed == null)
        enemyRed = new TextureRegion(new Texture("Tcell.png"));
    if (enemyYellow == null)
        enemyYellow = new TextureRegion(new Texture("Tcell.png"));
    return isHit ? enemyYellow : enemyRed;
}

    public static TextureRegion createWallTexture(int size) {
    TextureRegion cached = wallBySize.get(size);
    if (cached != null) return cached;
    TextureRegion region = new TextureRegion(new Texture("Normal_cell.png"));
    wallBySize.put(size, region);
    return region;
}

    public static TextureRegion createBoundaryTexture(int width, int height) {
        String key = width + "x" + height;
        TextureRegion cached = boundaryBySize.get(key);
        if (cached != null) return cached;

        TextureRegion region = createSolidTexture(width, height, Color.GRAY);
        boundaryBySize.put(key, region);
        return region;
    }

    private static TextureRegion createSolidTexture(int w, int h, Color color) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegion region = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
        return region;
    }

    public static void disposeAll() {
        Set<Texture> disposed = new HashSet<>();
        disposeRegion(cancerCell, disposed);
        disposeRegion(enemyRed, disposed);
        disposeRegion(enemyYellow, disposed);
        for (TextureRegion r : wallBySize.values()) disposeRegion(r, disposed);
        for (TextureRegion r : boundaryBySize.values()) disposeRegion(r, disposed);

        cancerCell = null;
        enemyRed = null;
        enemyYellow = null;
        wallBySize.clear();
        boundaryBySize.clear();
    }

    private static void disposeRegion(TextureRegion region, Set<Texture> disposed) {
        if (region == null || region.getTexture() == null) return;
        Texture tex = region.getTexture();
        if (disposed.add(tex)) tex.dispose();
    }
}