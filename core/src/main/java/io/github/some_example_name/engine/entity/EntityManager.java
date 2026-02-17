package io.github.some_example_name.engine.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.some_example_name.engine.util.Validation;
  
public class EntityManager implements IEntityManager {

    private final Map<UUID, Entity> entities = new LinkedHashMap<>();
    private final List<Entity> pendingAdd = new ArrayList<>();
    private final Set<UUID> pendingRemove = new LinkedHashSet<>();
    private boolean updating = false;

    @Override
    public UUID create(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Cannot create null entity");
        }

        if (updating) {
            pendingAdd.add(entity);
            return entity.getId();
        }

        entities.put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public Entity remove(UUID id) {
        if (id == null) return null;

        Entity existing = entities.get(id);
        if (existing == null) return null;

        if (updating) {
            pendingRemove.add(id);
            return existing;
        }

        return entities.remove(id);
    }

    @Override
    public Entity get(UUID id) {
        if (id == null) return null;
        return entities.get(id);
    }

    @Override
    public void update(float deltaTime) {
        Validation.requireValidDelta(deltaTime);

        updating = true;
        try {
            for (Entity entity : new ArrayList<>(entities.values())) {
                if (entity != null && entity.isActive()) {
                    entity.update(deltaTime);
                }
            }
        } finally {
            updating = false;
            flushPendingChanges();
        }
    }

    @Override
    public Collection<Entity> getAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(entities.values()));
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public boolean contains(UUID id) {
        return id != null && entities.containsKey(id);
    }

    @Override
    public void clear() {
        entities.clear();
        pendingAdd.clear();
        pendingRemove.clear();
        updating = false;
    }

    private void flushPendingChanges() {
        if (!pendingRemove.isEmpty()) {
            for (UUID id : pendingRemove) {
                entities.remove(id);
            }
            pendingRemove.clear();
        }

        if (!pendingAdd.isEmpty()) {
            for (Entity entity : pendingAdd) {
                entities.put(entity.getId(), entity);
            }
            pendingAdd.clear();
        }
    }
}