package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.engine.io.EngineServices;

final class MovementLegendTextures {
    final Texture shift;
    final Texture w;
    final Texture a;
    final Texture s;
    final Texture d;
    final Texture up;
    final Texture left;
    final Texture down;
    final Texture right;

    private MovementLegendTextures(EngineServices services) {
        shift = services.getAssets().getTexture("key-gui/movement/shift.png");
        w = services.getAssets().getTexture("key-gui/movement/w.png");
        a = services.getAssets().getTexture("key-gui/movement/a.png");
        s = services.getAssets().getTexture("key-gui/movement/s.png");
        d = services.getAssets().getTexture("key-gui/movement/d.png");
        up = services.getAssets().getTexture("key-gui/movement/arrow_up.png");
        left = services.getAssets().getTexture("key-gui/movement/arrow_left.png");
        down = services.getAssets().getTexture("key-gui/movement/arrow_down.png");
        right = services.getAssets().getTexture("key-gui/movement/arrow_right.png");
    }

    static MovementLegendTextures load(EngineServices services) {
        if (services == null) {
            throw new IllegalArgumentException("EngineServices cannot be null");
        }
        return new MovementLegendTextures(services);
    }
}
