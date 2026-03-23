package io.github.some_example_name.game.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.io.DynamicInput;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.game.movement.PlayerMovement;

public class CancerCell extends GameEntity {

    private final HealthBar healthBar;
    private final DynamicInput input;
    private final PlayerMovement playerMovement;
    private Animation<TextureRegion> walkAnimation;

    private static final float STARTING_SIZE = 90f;
    private static final float EXP_PER_LEVEL = 100f;

    private float exp = 0f;
    private float stateTime = 0f;
    private float expToNextLevel = EXP_PER_LEVEL;
    private int level = 1;

    public CancerCell(DynamicInput input, float x, float y) {
        super(x, y, STARTING_SIZE);
        if (input == null) throw new IllegalArgumentException("DynamicInput cannot be null");
        applySize(STARTING_SIZE);
        this.input = input;
        this.healthBar = new HealthBar(this, STARTING_SIZE, 5f, 4f);
        this.playerMovement = new PlayerMovement(new MovementManager());
        Texture sheet = new Texture("cancer_cell.png");
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new TextureRegion(sheet, i * 64, 0, 64, 64);
        }
        walkAnimation = new Animation<>(0.15f, frames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.texture = walkAnimation.getKeyFrame(0f);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        this.texture = walkAnimation.getKeyFrame(stateTime);

        playerMovement.update(deltaTime);
        playerMovement.movePlayer(this, 200f, deltaTime, key -> input.isKeyPressed(key));

        float x = Math.max(64f, Math.min(getPositionX(), 2000f - 64f - getWidth()));
        float y = Math.max(64f, Math.min(getPositionY(), 2000f - 64f - getHeight()));
        setPosition(x, y);
    }

    @Override
    public void onCollision(Collidable other) {
        // GameScene handles all collision logic — do nothing here
    }

    public void gainExp(float amount) {
        this.exp += amount;
        if (this.exp >= expToNextLevel) levelUp();
    }

    private void levelUp() {
        level++;
        exp = 0f;
        expToNextLevel *= 1.5f;
        float newSize = getSize() + 10f;
        applySize(newSize);
        healthBar.getOwner();
    }

    @Override public int getCollisionLayer() { return 1 << 0; }
    @Override public int getCollisionMask()  { return (1 << 1) | (1 << 2); }
    public HealthBar getHealthBar()          { return healthBar; }
    public int getLevel()                    { return level; }
    public float getExp()                    { return exp; }
    public float getExpToNextLevel()         { return expToNextLevel; }
    public TextureRegion getCurrentTexture() { return texture; }
}