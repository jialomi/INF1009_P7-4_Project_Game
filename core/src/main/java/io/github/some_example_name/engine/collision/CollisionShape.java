package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents the collision shape of an entity.
 * Supports CIRCLE and RECTANGLE shapes.
 */
public abstract class CollisionShape {

    public enum Type { CIRCLE, RECTANGLE }

    public abstract Type getType();
    public abstract float getCenterX();
    public abstract float getCenterY();

    public static CollisionShape circle(float centerX, float centerY, float radius) {
        return new CircleShape(centerX, centerY, radius);
    }

    public static CollisionShape rectangle(Rectangle rect) {
        return new RectShape(rect);
    }

    public static final class CircleShape extends CollisionShape {
        public final float cx, cy, radius;
        CircleShape(float cx, float cy, float radius) {
            this.cx = cx; this.cy = cy; this.radius = radius;
        }
        @Override
        public Type getType() {
            return Type.CIRCLE;
        }
        @Override
        public float getCenterX() {
            return cx;
        }
        @Override
        public float getCenterY() {
            return cy;
        }
    }

    public static final class RectShape extends CollisionShape {
        public final Rectangle rect;
        RectShape(Rectangle rect) {
            this.rect = rect;
        }
        @Override
        public Type getType() {
            return Type.RECTANGLE;
        }
        @Override
        public float getCenterX() {
            return rect.x + rect.width * 0.5f;
        }
        @Override
        public float getCenterY() {
            return rect.y + rect.height * 0.5f;
        }
    }
}