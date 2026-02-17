package io.github.some_example_name.engine.collision;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollisionManager {
    private final List<Collidable> collidables = new ArrayList<>();

    public void addCollidable(Collidable c) {
        if (c == null || collidables.contains(c)) return;
        collidables.add(c);
    }

    public void removeCollidable(Collidable c) {
        if (c == null) return;
        collidables.remove(c);
    }

    public void update() {
        List<Collidable> snapshot = new ArrayList<>(collidables);

        for (int i = 0; i < snapshot.size(); i++) {
            for (int j = i + 1; j < snapshot.size(); j++) {
                Collidable a = snapshot.get(i);
                Collidable b = snapshot.get(j);

                if (a != null && b != null && checkOverlap(a, b)) {
                    resolve(a, b);
                }
            }
        }
    }

    public int size() {
        return collidables.size();
    }

    public void clear() {
        collidables.clear();
    }

    public List<Collidable> getAll() {
        return Collections.unmodifiableList(collidables);
    }

    private boolean checkOverlap(Collidable a, Collidable b) {
        Rectangle aBounds = a.getBounds();
        Rectangle bBounds = b.getBounds();
        return aBounds != null && bBounds != null && aBounds.overlaps(bBounds);
    }

    private void resolve(Collidable a, Collidable b) {
        a.onCollision(b);
        b.onCollision(a);
    }
}
