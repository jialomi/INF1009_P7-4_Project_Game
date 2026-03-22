package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class RenderableEntity extends Entity implements Renderable {
    
    protected TextureRegion texture;
    protected Rectangle bounds;
    
    public RenderableEntity(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    public Rectangle getBounds() {
        bounds.setPosition(getPositionX(), getPositionY());
        return bounds;
    }
    
    @Override
    public TextureRegion getTexture() { return texture; }

    public void setTexture(TextureRegion texture) { this.texture = texture; }
    
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (this.bounds != null) {
            this.bounds.setSize(width, height);
        }
    }
}
