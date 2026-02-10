package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

// === ISP FIX: Removed 'implements Collidable' ===
// Now this class has ONE responsibility: Rendering.
public abstract class RenderableEntity extends Entity {
    
    protected float width;
    protected float height;
    protected TextureRegion texture;
    protected Rectangle bounds;
    
    public RenderableEntity(float x, float y, float width, float height) {
        super(x, y); 
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }
    
    // We keep this helper because it's useful, but we don't force the Interface anymore.
    public Rectangle getBounds() {
        bounds.setPosition(getPositionX(), getPositionY());
        return bounds;
    }
    
    // ===== Entity Implementation =====
    @Override
    public TextureRegion getTexture() { return texture; }
    
    @Override
    public float getWidth() { return width; }
    
    @Override
    public float getHeight() { return height; }
    
    // ===== Setters =====
    public void setTexture(TextureRegion texture) { this.texture = texture; }
    
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        if (this.bounds != null) {
            this.bounds.setSize(width, height);
        }
    }
}