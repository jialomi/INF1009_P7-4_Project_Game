package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.NpcBehaviour;

public class TCell extends GameEntity {
    private static final float TCELL_SIZE = 56f;
    private static final float CHASE_RANGE = 480f;
    private static final float DISENGAGE_RANGE = 580f;
    private static final float WANDER_INTERVAL = 1.6f;
    private static final float CHASE_SPEED_MULTIPLIER = 1.15f;
    private static final float WANDER_SPEED_MULTIPLIER = 0.8f;
    private static final float FLANK_DISTANCE = 275f;
    private static final float DIRECT_ENGAGE_DISTANCE = 100f;

    private final HealthBar healthBar;
    private final Animation<TextureRegion> walkAnimation;
    private final MovementManager movementManager;
    private final float speed;
    private final PursuitMode pursuitMode;

    private float animationTime = 0f;
    private Vector2 wanderDir = new Vector2(1f, 0f);
    private float wanderTimer;
    private CancerCell target;
    private boolean pursuingTarget;

    private enum PursuitMode {
        DIRECT(0f),
        FLANK_LEFT(-1f),
        FLANK_RIGHT(1f);

        private final float flankSide;

        PursuitMode(float flankSide) {
            this.flankSide = flankSide;
        }
    }

    public TCell(float x, float y, float speed) {
        super(x, y, TCELL_SIZE, TCELL_SIZE);
        applySize(TCELL_SIZE);
        setHitboxInsets(8f, 8f, 5f, 8f);
        setUseCircularHitbox(true);
        this.healthBar = new HealthBar(this, TCELL_SIZE, 5f, 4f);
        this.speed = speed;
        this.walkAnimation = CellAssets.getTCellWalkAnimation();
        this.movementManager = new MovementManager();
        this.pursuitMode = randomPursuitMode();
        this.texture = walkAnimation.getKeyFrame(0f);
    }

    @Override
    public void update(float deltaTime) {
        animationTime += deltaTime;
        this.texture = walkAnimation.getKeyFrame(animationTime);

        if (!isActive()) {
            return;
        }

        wanderTimer += deltaTime;
        wanderDir = NpcBehaviour.updateWanderDirection(wanderDir, wanderTimer, WANDER_INTERVAL);
        if (wanderTimer >= WANDER_INTERVAL) {
            wanderTimer = 0f;
        }

        float targetDistance = distanceToTarget();
        if (target != null) {
            if (!pursuingTarget && targetDistance <= CHASE_RANGE) {
                pursuingTarget = true;
            } else if (pursuingTarget && targetDistance > DISENGAGE_RANGE) {
                pursuingTarget = false;
            }
        } else {
            pursuingTarget = false;
        }

        if (pursuingTarget && target != null) {
            pursueTarget(deltaTime);
        } else {
            NpcBehaviour.wander(this, wanderDir, speed * WANDER_SPEED_MULTIPLIER, deltaTime, movementManager);
        }
    }

    public void setTarget(CancerCell target) {
        this.target = target;
    }

    @Override
    public void onCollision(Collidable other) {
        // GameScene handles gameplay outcomes.
    }

    @Override
    public int getCollisionLayer() {
        return GameCollisionLayers.IMMUNE_CELL;
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

    public TextureRegion getCurrentTexture() {
        return texture;
    }

    private float distanceToTarget() {
        if (target == null) {
            return Float.MAX_VALUE;
        }
        float dx = (target.getPositionX() + target.getWidth() * 0.5f)
                - (getPositionX() + getWidth() * 0.5f);
        float dy = (target.getPositionY() + target.getHeight() * 0.5f)
                - (getPositionY() + getHeight() * 0.5f);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void pursueTarget(float deltaTime) {
        if (target == null) {
            return;
        }

        float moveSpeed = speed * CHASE_SPEED_MULTIPLIER;
        if (pursuitMode == PursuitMode.DIRECT || distanceToTarget() <= DIRECT_ENGAGE_DISTANCE) {
            NpcBehaviour.chase(this, target, moveSpeed, deltaTime, movementManager);
            return;
        }

        Vector2 from = movementManager.getEntityCenter(this);
        Vector2 targetCenter = movementManager.getEntityCenter(target);
        Vector2 toTarget = new Vector2(targetCenter.x - from.x, targetCenter.y - from.y);
        if (toTarget.len2() <= 0.0001f) {
            NpcBehaviour.chase(this, target, moveSpeed, deltaTime, movementManager);
            return;
        }

        Vector2 perpendicular = new Vector2(-toTarget.y, toTarget.x).nor()
                .scl(FLANK_DISTANCE * pursuitMode.flankSide);
        Vector2 flankPoint = new Vector2(targetCenter).add(perpendicular);
        NpcBehaviour.moveTowardPoint(this, flankPoint.x, flankPoint.y, moveSpeed, deltaTime, movementManager);
    }

    private PursuitMode randomPursuitMode() {
        double roll = Math.random();
        if (roll < 0.34d) {
            return PursuitMode.DIRECT;
        }
        if (roll < 0.67d) {
            return PursuitMode.FLANK_LEFT;
        }
        return PursuitMode.FLANK_RIGHT;
    }
}
