package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.NpcBehaviour;

public class TCell extends GameEntity {

    private final HealthBar healthBar;
    private final MovementManager movementManager;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime = 0f;

    private static final float TCELL_SIZE      = 90f;
    private static final float WANDER_INTERVAL = 2f;
    private static final float CHASE_RANGE     = 3000f;
    private float speed;

    private Vector2 wanderDir   = new Vector2(1, 0);
    private float   wanderTimer = 0f;
    private CancerCell target;

    public TCell(float x, float y, float speed) {
        super(x, y, TCELL_SIZE);
        applySize(TCELL_SIZE);
        this.healthBar       = new HealthBar(this, TCELL_SIZE, 5f, 4f);
        this.movementManager = new MovementManager();
        this.speed = speed;

        Texture sheet = new Texture("tcell_strip.png");
        TextureRegion[] frames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            frames[i] = new TextureRegion(sheet, i * 64, 0, 64, 64);
        }
        walkAnimation = new Animation<>(0.12f, frames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.texture = walkAnimation.getKeyFrame(0f);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        this.texture = walkAnimation.getKeyFrame(stateTime);

        // Only deactivate if explicitly set inactive — not HP death
        if (!isActive()) return;

        wanderTimer += deltaTime;
        wanderDir = NpcBehaviour.updateWanderDirection(wanderDir, wanderTimer, WANDER_INTERVAL);
        if (wanderTimer >= WANDER_INTERVAL) wanderTimer = 0f;

        if (target != null && movementManager.getDistanceBetween(this, target) < CHASE_RANGE) {
            NpcBehaviour.chase(this, target, speed, deltaTime, movementManager);
        } else {
            NpcBehaviour.wander(this, wanderDir, speed, deltaTime, movementManager);
        }

        // Clamps cells inside the massive 2000x2000 world, 
        // keeping a 64px buffer so they don't slide under the UI frame!
        float x = Math.max(64f, Math.min(getPositionX(), 2000f - 64f - getWidth()));
        float y = Math.max(64f, Math.min(getPositionY(), 2000f - 64f - getHeight()));
        setPosition(x, y);
    }

    public void setTarget(CancerCell target) { this.target = target; }

    @Override
    public void onCollision(Collidable other) {
        // GameScene handles all collision logic — do nothing here
    }

    @Override public int getCollisionLayer() { return 1 << 2; }
    @Override public int getCollisionMask()  { return 1 << 0; }
    public HealthBar getHealthBar()          { return healthBar; }
    // Add this getter at the bottom with the other getters:
    public TextureRegion getCurrentTexture() { return texture; }

    
}