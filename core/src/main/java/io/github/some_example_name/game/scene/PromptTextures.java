package io.github.some_example_name.game.scene;

import com.badlogic.gdx.graphics.Texture;

import io.github.some_example_name.engine.io.EngineServices;

final class PromptTextures {
    final Texture enter;
    final Texture escape;
    final Texture p;
    final Texture r;
    final Texture o;

    private PromptTextures(EngineServices services) {
        enter = services.getAssets().getTexture("key-gui/settingKeys/enter.png");
        escape = services.getAssets().getTexture("key-gui/settingKeys/escape.png");
        p = services.getAssets().getTexture("key-gui/settingKeys/p.png");
        r = services.getAssets().getTexture("key-gui/settingKeys/r.png");
        o = services.getAssets().getTexture("key-gui/settingKeys/o.png");
    }

    static PromptTextures load(EngineServices services) {
        if (services == null) {
            throw new IllegalArgumentException("EngineServices cannot be null");
        }
        return new PromptTextures(services);
    }
}
