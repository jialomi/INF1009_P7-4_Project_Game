package io.github.some_example_name.game.entity;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.NpcBehaviour;

public class NormalCell extends GameEntity {

    private final HealthBar healthBar;
    private final MovementManager movementManager;

    private static final float NORMAL_SIZE      = 32f;
    private static final float DAMAGE_TO_CANCER = 5f;

    private Vector2 wanderDir = new Vector2(0, -1);
    private float wanderTimer = 0f;
    private static final float WANDER_INTERVAL = 2f;
    private float fleeRange;
    private float speed;
    private CancerCell threat;

    public NormalCell(float x, float y, float speed, float fleeRange) {
        super(x, y, NORMAL_SIZE);
        applySize(NORMAL_SIZE);
        this.speed = speed;
        this.fleeRange = fleeRange;
        this.healthBar     = new HealthBar(this, NORMAL_SIZE, 5f, 4f);
        this.movementManager = new MovementManager();
        this.texture = TextureFactory.createWallTexture((int) NORMAL_SIZE);
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive()) {
            setActive(false);
            return;
        }
        wanderTimer += deltaTime;
        wanderDir = NpcBehaviour.updateWanderDirection(wanderDir, wanderTimer, WANDER_INTERVAL);
        if (wanderTimer >= WANDER_INTERVAL) wanderTimer = 0f;

        if (threat != null && movementManager.getDistanceBetween(this, threat) < fleeRange) {
            NpcBehaviour.flee(this, threat, speed, deltaTime, movementManager);
        } else {
            NpcBehaviour.wander(this, wanderDir, speed, deltaTime, movementManager);
        }

        // Clamps cells inside the massive 2000x2000 world, 
        // keeping a 64px buffer so they don't slide under the UI frame!
        float x = Math.max(64f, Math.min(getPositionX(), 2000f - 64f - getWidth()));
        float y = Math.max(64f, Math.min(getPositionY(), 2000f - 64f - getHeight()));
        setPosition(x, y);
    }

    public void setThreat(CancerCell threat) { this.threat = threat; }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof CancerCell) {
            CancerCell cancer = (CancerCell) other;
            if (cancer.canEat(this)) {
                takeDamage(cancer.getDamage());
                cancer.takeDamage(DAMAGE_TO_CANCER);
            }
        }
    }

    @Override public int getCollisionLayer() { return 1 << 1; }
    @Override public int getCollisionMask()  { return 1 << 0; }
    public HealthBar getHealthBar()          { return healthBar; }
}