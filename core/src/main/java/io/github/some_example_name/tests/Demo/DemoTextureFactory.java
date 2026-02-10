package io.github.some_example_name.tests.Demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DemoTextureFactory {

    public static TextureRegion createPlayerTexture() {
        return createSolidTexture(64, 64, Color.BLUE);
    }

    public static TextureRegion createEnemyTexture(boolean isHit) {
        return createSolidTexture(48, 48, isHit ? Color.YELLOW : Color.RED);
    }

    public static TextureRegion createWallTexture(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0); // Transparent
        pixmap.fill();
        pixmap.setColor(Color.PURPLE);
        pixmap.fillCircle(size/2, size/2, size/2);
        
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(tex);
    }

    private static TextureRegion createSolidTexture(int w, int h, Color color) {
        // === ERROR HANDLING DEMO ===
        try {
            Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            Texture tex = new Texture(pixmap);
            pixmap.dispose();
            return new TextureRegion(tex);
        } catch (Exception e) {
            // If something goes wrong (e.g., OutOfMemory), catch it!
            System.err.println("CRITICAL ERROR: Failed to create texture!");
            e.printStackTrace();
            return null; // Return null so game continues (or handle gracefully)
        }
    }
}