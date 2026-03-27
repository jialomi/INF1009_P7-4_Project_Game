package io.github.some_example_name.game.entity;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.engine.collision.CollisionShape;
import io.github.some_example_name.engine.collision.PhysicalBody;
import io.github.some_example_name.engine.entity.RenderableEntity;

public abstract class GameEntity extends RenderableEntity implements PhysicalBody {
    private float hitboxInsetLeft;
    private float hitboxInsetRight;
    private float hitboxInsetBottom;
    private float hitboxInsetTop;
    private boolean useCircularHitbox;
    private float hp;
    private float maxHp;
    private float size;
    private float damage;

    protected GameEntity(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.size = Math.max(width, height);
    }

    public void applySize(float newSize) {
        applySize(newSize, newSize, newSize);
    }

    protected void applySize(float progressionSize, float width, float height) {
        float hpRatio = maxHp > 0f ? hp / maxHp : 1f;
        this.size = progressionSize;
        setSize(width, height);
        this.maxHp = progressionSize * 2f;
        this.hp = maxHp * hpRatio;
        this.damage = progressionSize * 0.5f;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateBounds();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateBounds();
    }

    @Override
    public CollisionShape getCollisionShape() {
        updateBounds();
        if (useCircularHitbox) {
            float radius = Math.min(bounds.width, bounds.height) * 0.5f;
            return CollisionShape.circle(
                    bounds.x + bounds.width * 0.5f,
                    bounds.y + bounds.height * 0.5f,
                    radius);
        }
        return CollisionShape.rectangle(bounds);
    }

    protected void setHitboxInsets(float left, float right, float bottom, float top) {
        if (left < 0f || right < 0f || bottom < 0f || top < 0f) {
            throw new IllegalArgumentException("Hitbox insets must be >= 0.");
        }
        if (left + right >= getWidth() || bottom + top >= getHeight()) {
            throw new IllegalArgumentException("Hitbox insets are too large for the entity size.");
        }
        this.hitboxInsetLeft = left;
        this.hitboxInsetRight = right;
        this.hitboxInsetBottom = bottom;
        this.hitboxInsetTop = top;
        updateBounds();
    }

    protected void setUseCircularHitbox(boolean useCircularHitbox) {
        this.useCircularHitbox = useCircularHitbox;
    }

    public boolean usesCircularHitbox() {
        return useCircularHitbox;
    }

    public boolean overlaps(GameEntity other) {
        if (other == null) {
            return false;
        }

        CollisionShape thisShape = getCollisionShape();
        CollisionShape otherShape = other.getCollisionShape();
        if (thisShape == null || otherShape == null) {
            return false;
        }

        if (thisShape.getType() == CollisionShape.Type.CIRCLE
                && otherShape.getType() == CollisionShape.Type.CIRCLE) {
            return circlesOverlap(
                    (CollisionShape.CircleShape) thisShape,
                    (CollisionShape.CircleShape) otherShape);
        }
        if (thisShape.getType() == CollisionShape.Type.CIRCLE
                && otherShape.getType() == CollisionShape.Type.RECTANGLE) {
            return circleRectOverlap(
                    (CollisionShape.CircleShape) thisShape,
                    ((CollisionShape.RectShape) otherShape).rect);
        }
        if (thisShape.getType() == CollisionShape.Type.RECTANGLE
                && otherShape.getType() == CollisionShape.Type.CIRCLE) {
            return circleRectOverlap(
                    (CollisionShape.CircleShape) otherShape,
                    ((CollisionShape.RectShape) thisShape).rect);
        }

        return getBounds().overlaps(other.getBounds());
    }

    public void takeDamage(float amount) {
        this.hp -= amount;
        if (this.hp < 0f) {
            this.hp = 0f;
        }
    }

    public boolean canEat(GameEntity other) {
        return this.size > other.size;
    }

    public boolean isAlive() {
        return this.hp > 0f && isActive();
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public float getSize() {
        return size;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public com.badlogic.gdx.math.Rectangle getBounds() {
        updateBounds();
        return bounds;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void heal(float amount) {
        if (amount <= 0f) {
            return;
        }
        this.hp = Math.min(maxHp, hp + amount);
    }

    private void updateBounds() {
        float x = getPositionX() + hitboxInsetLeft;
        float y = getPositionY() + hitboxInsetBottom;
        float width = getWidth() - hitboxInsetLeft - hitboxInsetRight;
        float height = getHeight() - hitboxInsetBottom - hitboxInsetTop;
        bounds.set(x, y, width, height);
    }

    private boolean circlesOverlap(CollisionShape.CircleShape a, CollisionShape.CircleShape b) {
        float dx = b.cx - a.cx;
        float dy = b.cy - a.cy;
        float radiusSum = a.radius + b.radius;
        return dx * dx + dy * dy <= radiusSum * radiusSum;
    }

    private boolean circleRectOverlap(CollisionShape.CircleShape circle, Rectangle rect) {
        float closestX = clamp(circle.cx, rect.x, rect.x + rect.width);
        float closestY = clamp(circle.cy, rect.y, rect.y + rect.height);
        float dx = closestX - circle.cx;
        float dy = closestY - circle.cy;
        return dx * dx + dy * dy <= circle.radius * circle.radius;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
