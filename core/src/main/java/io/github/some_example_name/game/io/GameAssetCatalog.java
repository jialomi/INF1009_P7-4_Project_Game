package io.github.some_example_name.game.io;

import io.github.some_example_name.engine.io.AssetService;
import io.github.some_example_name.engine.io.AudioOutput;

public final class GameAssetCatalog {
    private static final String[] TEXTURES = {
            // game textures
            "bg.png",
            "wall_tile.png",
            "cancer_cell.png",
            "Normal_cell.png",
            "tcell_strip.png",

            // key gui - setting keys
            "key-gui/settingKeys/enter.png",
            "key-gui/settingKeys/escape.png",
            "key-gui/settingKeys/f3.png",
            "key-gui/settingKeys/numpad_enter.png",
            "key-gui/settingKeys/o.png",
            "key-gui/settingKeys/p.png",
            "key-gui/settingKeys/r.png",

            // Key GUI - Movement
            "key-gui/movement/a.png",
            "key-gui/movement/arrow_down.png",
            "key-gui/movement/arrow_left.png",
            "key-gui/movement/arrow_right.png",
            "key-gui/movement/arrow_up.png",
            "key-gui/movement/d.png",
            "key-gui/movement/s.png",
            "key-gui/movement/shift.png",
            "key-gui/movement/w.png",
    };

    private static final String[] SOUNDS = {
            "crash.mp3",
            "damage.mp3",
            "squelch.mp3"
    };

    private static final String[] MUSIC = {
            "lungs_bgm.mp3"
    };

    private GameAssetCatalog() {
    }

    public static void preloadAll(AssetService assets, AudioOutput audio) {
        if (assets == null || audio == null) {
            throw new IllegalArgumentException("AssetService and AudioOutput cannot be null.");
        }
        for (String texture : TEXTURES) {
            if (!assets.isLoaded(texture, com.badlogic.gdx.graphics.Texture.class)) {
                assets.loadTextureNow(texture);
            }
        }
        for (String sound : SOUNDS) {
            audio.preloadSound(sound);
        }
        for (String music : MUSIC) {
            if (assets.exists(music) && !assets.isLoaded(music, com.badlogic.gdx.audio.Music.class)) {
                assets.loadMusicNow(music);
            }
        }
    }
}
