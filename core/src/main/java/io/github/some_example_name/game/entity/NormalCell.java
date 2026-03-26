package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.NpcBehaviour;

public class NormalCell extends GameEntity {
    private static final float NORMAL_SIZE = 36f;
    private static final float WANDER_INTERVAL = 2f;
    private static final float STARTUP_SETTLE_TIME = 0.2f;

    private final HealthBar healthBar;
    private final MovementManager movementManager;
    private final float speed;
    private final float fleeRange;
    private Vector2 wanderDir;
    private float wanderTimer;
    private float startupDelay;
    private CancerCell threat;

    public NormalCell(float x, float y, float speed, float fleeRange) {
        super(x, y, NORMAL_SIZE, NORMAL_SIZE);
        applySize(NORMAL_SIZE);
        setHitboxInsets(3f, 3f, 3f, 3f);
        setUseCircularHitbox(true);
        this.healthBar = new HealthBar(this, NORMAL_SIZE, 5f, 4f);
        this.movementManager = new MovementManager();
        this.speed = speed;
        this.fleeRange = fleeRange;
        this.wanderDir = NpcBehaviour.randomDirection();
        this.wanderTimer = (float) (Math.random() * WANDER_INTERVAL);
        this.startupDelay = (float) (Math.random() * STARTUP_SETTLE_TIME);
        this.texture = new TextureRegion(CellAssets.getNormalCellTexture());
    }

    @Override
    public void update(float deltaTime) {
        if (!isAlive()) {
            return;
        }
        if (threat != null && overlaps(threat)) {
            return;
        }
        if (startupDelay > 0f) {
            startupDelay = Math.max(0f, startupDelay - deltaTime);
            return;
        }

        wanderTimer += deltaTime;
        wanderDir = NpcBehaviour.updateWanderDirection(wanderDir, wanderTimer, WANDER_INTERVAL);
        if (wanderTimer >= WANDER_INTERVAL) {
            wanderTimer = 0f;
        }

        if (threat != null && movementManager.getDistanceBetween(this, threat) < fleeRange) {
            NpcBehaviour.flee(this, threat, speed, deltaTime, movementManager);
        } else {
            NpcBehaviour.wander(this, wanderDir, speed, deltaTime, movementManager);
        }
    }

    @Override
    public void onCollision(Collidable other) {
        // GameScene handles gameplay outcomes.
    }

    @Override
    public int getCollisionLayer() {
        return GameCollisionLayers.HEALTHY_CELL;
    }

    @Override
    public int getCollisionMask() {
        return GameCollisionLayers.PLAYER;
    }

    @Override
    public boolean isSensor() {
        return true;
    }

    public HealthBar getHealthBar() {
        return healthBar;
    }

    public void setThreat(CancerCell threat) {
        this.threat = threat;
    }
}
