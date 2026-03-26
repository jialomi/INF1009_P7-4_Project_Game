package io.github.some_example_name.game.entity;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class AnimationStateController<S extends Enum<S>> {
    private final Map<S, Animation<TextureRegion>> animations;
    private final Map<S, TextureRegion> stillFrames;
    private S currentState;
    private float stateTime;

    public AnimationStateController(Class<S> stateType, S initialState) {
        if (stateType == null || initialState == null) {
            throw new IllegalArgumentException("stateType and initialState cannot be null.");
        }
        this.animations = new EnumMap<>(stateType);
        this.stillFrames = new EnumMap<>(stateType);
        this.currentState = initialState;
        this.stateTime = 0f;
    }

    public void setAnimation(S state, Animation<TextureRegion> animation) {
        animations.put(state, animation);
    }

    public void setStillFrame(S state, TextureRegion frame) {
        stillFrames.put(state, frame);
    }

    public void setState(S newState, boolean resetTime) {
        if (newState == null) {
            throw new IllegalArgumentException("newState cannot be null.");
        }
        if (newState != currentState) {
            currentState = newState;
            if (resetTime) {
                stateTime = 0f;
            }
        }
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> animation = animations.get(currentState);
        if (animation != null) {
            return animation.getKeyFrame(stateTime);
        }
        return stillFrames.get(currentState);
    }
}
