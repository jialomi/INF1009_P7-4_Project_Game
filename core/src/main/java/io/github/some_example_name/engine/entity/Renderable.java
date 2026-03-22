package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Rendering contract for entities that have a visual representation.
 */
public interface Renderable {
    TextureRegion getTexture();
}
