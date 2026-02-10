package io.github.some_example_name.tests.unit;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionManager;

public class TestCollision extends ApplicationAdapter {
    private ShapeRenderer shape;
    private CollisionManager collisionManager;
    private Circle circle;
    private Triangle triangle;

    @Override
    public void create() {
        shape = new ShapeRenderer();

        // Test objects
        circle = new Circle(100, 100, 40, Color.RED);
        triangle = new Triangle(400, 300, 60, Color.GREEN);

        // Initialize collision manager
        collisionManager = new CollisionManager();
        
        // Register objects as collidables
        collisionManager.addCollidable(circle);
        collisionManager.addCollidable(triangle);

        System.out.println("Collision test started. Press ARROWS to move circle. Triangle will turn YELLOW when hit.");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        float delta = Gdx.graphics.getDeltaTime();

        // Update circle position
        circle.update(delta);

        // Reset triangle to original color
        triangle.resetColor();

        // Check for collisions
        collisionManager.update();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        circle.draw(shape);
        triangle.draw(shape);
        shape.end();
    }

    @Override
    public void dispose() {
        shape.dispose();
    }

    // Movable circle that implements Collidable
    class Circle implements Collidable {
        private float x, y;
        private float radius;
        private Color color;
        private float speed = 200f;
        private Rectangle bounds;

        public Circle(float x, float y, float radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.bounds = new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
        }

        public void update(float delta) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                y -= speed * delta;
            }
        }

        public void draw(ShapeRenderer shape) {
            shape.setColor(color);
            shape.circle(x, y, radius);
        }

        // Return bounding box for collision detection
        @Override
        public Rectangle getBounds() {
            bounds.setPosition(x - radius, y - radius);
            return bounds;
        }

        // No action needed for circle
        @Override
        public void onCollision(Collidable other) {
        }
    }

 // Static triangle that implements Collidable
    class Triangle implements Collidable {
        private float x, y;
        private float size;
        private Color color;
        private Color originalColor;
        private Rectangle bounds;

        public Triangle(float x, float y, float size, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.originalColor = color.cpy();
            this.bounds = new Rectangle(x, y, size, size);
        }

        public void draw(ShapeRenderer shape) {
            shape.setColor(color);
            shape.triangle(
                    x, y,
                    x + size, y,
                    x + size / 2, y + size);
        }

        public void resetColor() {
            color = originalColor;
        }

        // Return bounding box for collision detection
        @Override
        public Rectangle getBounds() {
            bounds.setPosition(x, y);
            return bounds;
        }

        // Change color when colliding
        @Override
        public void onCollision(Collidable other) {
            color = Color.YELLOW;
        }
    }
}