package io.github.some_example_name.game.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.movement.MovementManager;

public class NpcBehaviour {
    private NpcBehaviour() {}
    
    // Chase behaviour
    public static void chase(Entity npc, Entity target, float speed, float deltaTime, MovementManager mm) {
        Vector2 from = mm.getEntityCenter(npc);
        Vector2 to = mm.getEntityCenter(target);

        Vector2 direction = new Vector2(to.x - from.x, to.y - from.y);
        if (direction.len2() == 0f) return; 

        mm.moveByDirection(npc, direction, speed, deltaTime);
    }

    // Flee behaviour
    public static void flee(Entity npc, Entity threat, float speed, float deltaTime, MovementManager mm) {
        Vector2 from = mm.getEntityCenter(npc);
        Vector2 threatPos = mm.getEntityCenter(threat);

        Vector2 direction = new Vector2(from.x - threatPos.x, from.y - threatPos.y); // flipped vs chase
        if (direction.len2() == 0f) return;

        mm.moveByDirection(npc, direction, speed, deltaTime);
    }

    // Wander behaviour
    public static void wander(Entity npc, Vector2 direction, float speed, float deltaTime, MovementManager mm) {
        if (direction.len2() == 0f) return;
        mm.moveByDirection(npc, direction, speed, deltaTime);
    }

    public static Vector2 updateWanderDirection(Vector2 current, float timer, float interval) {
        if (timer >= interval) {
            float angle = (float) (Math.random() * Math.PI * 2);
            return new Vector2((float) Math.cos(angle), (float) Math.sin(angle));
        }
        return current;
    }
}
