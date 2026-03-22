package io.github.some_example_name.engine.entity;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.some_example_name.engine.util.Validation;
  
public class EntityManager implements IEntityManager {

    private static final float DEFAULT_SPATIAL_CELL_SIZE = 128f;
    private final Map<UUID, Entity> entities = new LinkedHashMap<>();
    private final List<PendingChange> pendingChanges = new ArrayList<>();
    private final Map<Long, Set<UUID>> spatialBuckets = new HashMap<>();
    private final float spatialCellSize;
    private boolean updating = false;
    private boolean spatialIndexDirty = true;

    public EntityManager() {
        this(DEFAULT_SPATIAL_CELL_SIZE);
    }

    public EntityManager(float spatialCellSize) {
        if (spatialCellSize <= 0f || Float.isNaN(spatialCellSize) || Float.isInfinite(spatialCellSize)) {
            throw new IllegalArgumentException("spatialCellSize must be finite and > 0");
        }
        this.spatialCellSize = spatialCellSize;
    }

    @Override
    public UUID create(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Cannot create null entity");
        }

        if (updating) {
            pendingChanges.add(PendingChange.add(entity));
            return entity.getId();
        }

        entities.put(entity.getId(), entity);
        entity.snapInterpolation();
        markSpatialIndexDirty();
        return entity.getId();
    }

    @Override
    public Entity remove(UUID id) {
        if (id == null) return null;

        Entity existing = entities.get(id);
        if (existing == null) return null;

        if (updating) {
            existing.setActive(false);
            pendingChanges.add(PendingChange.remove(id));
            markSpatialIndexDirty();
            return existing;
        }

        Entity removed = entities.remove(id);
        markSpatialIndexDirty();
        return removed;
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
                    entity.capturePreviousState();
                    entity.update(deltaTime);
                }
            }
        } finally {
            updating = false;
            flushPendingChanges();
            rebuildSpatialIndexIfNeeded();
        }
    }

    @Override
    public Collection<Entity> getAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(entities.values()));
    }

    @Override
    public Collection<Entity> getEntitiesInBounds(Rectangle area) {
        if (area == null) {
            return Collections.emptyList();
        }

        rebuildSpatialIndexIfNeeded();
        Set<UUID> candidateIds = new LinkedHashSet<>();
        int minCellX = worldToCell(area.x);
        int maxCellX = worldToCell(area.x + area.width);
        int minCellY = worldToCell(area.y);
        int maxCellY = worldToCell(area.y + area.height);

        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                Set<UUID> bucket = spatialBuckets.get(cellKey(cellX, cellY));
                if (bucket != null) {
                    candidateIds.addAll(bucket);
                }
            }
        }

        List<Entity> matches = new ArrayList<>();
        for (UUID id : candidateIds) {
            Entity entity = entities.get(id);
            Rectangle bounds = getSpatialBounds(entity);
            if (entity != null && bounds != null && bounds.overlaps(area)) {
                matches.add(entity);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    @Override
    public Collection<Entity> getNearbyEntities(float centerX, float centerY, float radius) {
        if (radius < 0f || Float.isNaN(radius) || Float.isInfinite(radius)) {
            throw new IllegalArgumentException("radius must be finite and >= 0");
        }

        Rectangle area = new Rectangle(centerX - radius, centerY - radius, radius * 2f, radius * 2f);
        float radiusSquared = radius * radius;
        List<Entity> matches = new ArrayList<>();

        for (Entity entity : getEntitiesInBounds(area)) {
            Rectangle bounds = getSpatialBounds(entity);
            if (bounds == null) {
                continue;
            }
            float entityCenterX = bounds.x + bounds.width * 0.5f;
            float entityCenterY = bounds.y + bounds.height * 0.5f;
            float dx = entityCenterX - centerX;
            float dy = entityCenterY - centerY;
            if (dx * dx + dy * dy <= radiusSquared) {
                matches.add(entity);
            }
        }

        return Collections.unmodifiableList(matches);
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
        pendingChanges.clear();
        spatialBuckets.clear();
        spatialIndexDirty = true;
        updating = false;
    }

    private void flushPendingChanges() {
        if (pendingChanges.isEmpty()) {
            return;
        }

        for (PendingChange change : pendingChanges) {
            if (change.type == PendingChangeType.REMOVE) {
                entities.remove(change.entityId);
            } else if (change.entity != null) {
                change.entity.snapInterpolation();
                entities.put(change.entity.getId(), change.entity);
            }
        }
        pendingChanges.clear();
        markSpatialIndexDirty();
    }

    private void rebuildSpatialIndexIfNeeded() {
        if (!spatialIndexDirty) {
            return;
        }

        spatialBuckets.clear();
        for (Entity entity : entities.values()) {
            Rectangle bounds = getSpatialBounds(entity);
            if (bounds == null) {
                continue;
            }

            int minCellX = worldToCell(bounds.x);
            int maxCellX = worldToCell(bounds.x + bounds.width);
            int minCellY = worldToCell(bounds.y);
            int maxCellY = worldToCell(bounds.y + bounds.height);

            for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
                for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                    spatialBuckets.computeIfAbsent(cellKey(cellX, cellY), ignored -> new LinkedHashSet<>())
                        .add(entity.getId());
                }
            }
        }
        spatialIndexDirty = false;
    }

    private Rectangle getSpatialBounds(Entity entity) {
        if (entity == null) {
            return null;
        }
        float width = Math.max(entity.getWidth(), 1f);
        float height = Math.max(entity.getHeight(), 1f);
        return new Rectangle(entity.getPositionX(), entity.getPositionY(), width, height);
    }

    private int worldToCell(float coordinate) {
        return (int) Math.floor(coordinate / spatialCellSize);
    }

    private long cellKey(int cellX, int cellY) {
        return (((long) cellX) << 32) ^ (cellY & 0xFFFFFFFFL);
    }

    private void markSpatialIndexDirty() {
        spatialIndexDirty = true;
    }

    private static final class PendingChange {
        private final PendingChangeType type;
        private final Entity entity;
        private final UUID entityId;

        private PendingChange(PendingChangeType type, Entity entity, UUID entityId) {
            this.type = type;
            this.entity = entity;
            this.entityId = entityId;
        }

        private static PendingChange add(Entity entity) {
            return new PendingChange(PendingChangeType.ADD, entity, entity.getId());
        }

        private static PendingChange remove(UUID entityId) {
            return new PendingChange(PendingChangeType.REMOVE, null, entityId);
        }
    }

    private enum PendingChangeType {
        ADD,
        REMOVE
    }
}
