package io.github.some_example_name.engine.scene;

import io.github.some_example_name.engine.collision.Collidable;
import io.github.some_example_name.engine.collision.CollisionManager;
import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.entity.EntityManager;
import io.github.some_example_name.engine.movement.MovementManager;
import io.github.some_example_name.engine.util.Validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract engine scene implementation.
 * - Manages generic simulation elements (entities, movement, collisions)
 */

public abstract class AbstractScene implements EngineScreen{
    protected final EntityManager entityManager;
    protected final MovementManager movementManager;
    protected final CollisionManager collisionManager;

    private final Set<Collidable> collidableRegistry;
    private boolean initialised;
    private boolean disposed;

    protected AbstractScene() {
        this.entityManager = new EntityManager();
        this.movementManager = new MovementManager();
        this.collisionManager = new CollisionManager();
        this.collidableRegistry = new HashSet<>();
        this.initialised = false;
        this.disposed = false;
    }

    @Override
    public final void initialise() {
        if (initialised) {
            return;
        }
        initialised = true;
        onInitialise();
    }

    /**
     * Optional extension hook for subclasses
     */
    
    protected void onInitialise() {
        // Optional override
    }
    
    public final UUID createEntity(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        UUID id = entityManager.create(entity);

        if (entity instanceof Collidable) {
            registerCollidable((Collidable) entity);
        }
        return id;
    }


    public final Entity removeEntity(UUID id) {
        Entity removed = entityManager.remove(id);

        if (removed instanceof Collidable) {
            unregisterCollidable((Collidable) removed);
        }

        return removed;
    }

    public final Collection<Entity> getEntities() {
        return entityManager.getAll();
    }

    @Override
    public final void update(float delta) {
        ensureInitialised();
        Validation.requireValidDelta(delta);

        entityManager.update(delta);
        collisionManager.update();

        onUpdate(delta);
    }

    /**
     * Optional extension hook for subclasses
     */
    protected void onUpdate(float delta) {
        // Optional override
    }

    @Override
    public abstract void render(float delta);

    @Override
    public void resize(int width, int height) {
        // Optional override
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        onDispose();

        for (Collidable c : new HashSet<>(collidableRegistry)) {
            collisionManager.removeCollidable(c);
        }
        collidableRegistry.clear();
        entityManager.clear();
    }

    protected void onDispose() {
        // Optional override
    }

    protected final void registerCollidable(Collidable collidable) {
        if (collidable != null && collidableRegistry.add(collidable)) {
            collisionManager.addCollidable(collidable);
        }
    }

    protected final void unregisterCollidable(Collidable collidable) {
        if (collidable != null && collidableRegistry.remove(collidable)) {
            collisionManager.removeCollidable(collidable);
        }
    }

    private void ensureInitialised() {
        if (!initialised) {
            throw new IllegalStateException("Scene must be initialised before update/render.");
        }
    }
}

        

