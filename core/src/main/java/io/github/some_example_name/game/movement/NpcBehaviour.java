package io.github.some_example_name.game.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class NpcBehaviour {
    private NpcBehaviour() {}

    public static Vector2 randomDirection() {
        float angle = (float) (Math.random() * Math.PI * 2f);
        return new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
    }

    public static void chase(Entity npc, Entity target, float speed, float deltaTime, MovementManager movementManager) {
        Vector2 to = movementManager.getEntityCenter(target);
        moveTowardPoint(npc, to.x, to.y, speed, deltaTime, movementManager);
    }

    public static void moveTowardPoint(Entity npc, float targetX, float targetY,
            float speed, float deltaTime, MovementManager movementManager) {
        Vector2 from = movementManager.getEntityCenter(npc);
        Vector2 direction = new Vector2(targetX - from.x, targetY - from.y);
        if (direction.len2() > 0f) {
            movementManager.moveByDirection(npc, direction, speed, deltaTime);
        }
    }

    public static void flee(Entity npc, Entity threat, float speed, float deltaTime, MovementManager movementManager) {
        Vector2 from = movementManager.getEntityCenter(npc);
        Vector2 threatPos = movementManager.getEntityCenter(threat);
        Vector2 direction = new Vector2(from.x - threatPos.x, from.y - threatPos.y);
        if (direction.len2() > 0f) {
            movementManager.moveByDirection(npc, direction, speed, deltaTime);
        }
    }

    public static void wander(Entity npc, Vector2 direction, float speed, float deltaTime, MovementManager movementManager) {
        if (direction.len2() > 0f) {
            movementManager.moveByDirection(npc, direction, speed, deltaTime);
        }
    }

    public static Vector2 updateWanderDirection(Vector2 current, float timer, float interval) {
        if (timer >= interval) {
            return randomDirection();
        }
        return current;
    }
}
