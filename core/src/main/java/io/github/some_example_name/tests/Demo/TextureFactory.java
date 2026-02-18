package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TextureFactory {
    private static TextureRegion player;
    private static TextureRegion enemyRed;
    private static TextureRegion enemyYellow;
    private static final Map<Integer, TextureRegion> wallBySize = new HashMap<>();
    private static final Map<String, TextureRegion> boundaryBySize = new HashMap<>();

    private TextureFactory() {}

    public static TextureRegion createPlayerTexture() {
        if (player == null) player = createSolidTexture(64, 64, Color.BLUE);
        return player;
    }

    public static TextureRegion createEnemyTexture(boolean isHit) {
        if (isHit) {
            if (enemyYellow == null) enemyYellow = createSolidTexture(48, 48, Color.YELLOW);
            return enemyYellow;
        }
        if (enemyRed == null) enemyRed = createSolidTexture(48, 48, Color.RED);
        return enemyRed;
    }

    public static TextureRegion createWallTexture(int size) {
        TextureRegion cached = wallBySize.get(size);
        if (cached != null) return cached;

        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(Color.PURPLE);
        pixmap.fillCircle(size / 2, size / 2, size / 2);

        TextureRegion region = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
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
        disposeRegion(player, disposed);
        disposeRegion(enemyRed, disposed);
        disposeRegion(enemyYellow, disposed);
        for (TextureRegion r : wallBySize.values()) disposeRegion(r, disposed);
        for (TextureRegion r : boundaryBySize.values()) disposeRegion(r, disposed);

        player = null;
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