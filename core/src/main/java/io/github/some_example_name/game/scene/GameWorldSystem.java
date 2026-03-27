package io.github.some_example_name.game.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.engine.io.OutputManager;
import io.github.some_example_name.engine.scene.AbstractScene;
import io.github.some_example_name.game.config.GameBalanceConfig;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.CellFactory;
import io.github.some_example_name.game.entity.GameEntity;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.util.CancerEvolutionManager;
import io.github.some_example_name.game.util.WaveManager;

final class GameWorldSystem {
    private final AbstractScene scene;
    private final WaveManager waveManager;
    private final CancerEvolutionManager cancerManager;

    private CancerCell player;

    GameWorldSystem(AbstractScene scene, WaveManager waveManager, CancerEvolutionManager cancerManager) {
        this.scene = scene;
        this.waveManager = waveManager;
        this.cancerManager = cancerManager;
    }

    void setPlayer(CancerCell player) {
        this.player = player;
    }

    void spawnInitialEntities() {
        maintainHealthyPopulation();
        for (int i = 0; i < 2; i++) {
            spawnTCell();
        }
    }

    void updateSpawning(float delta) {
        if (waveManager.shouldSpawnTCell(delta, cancerManager.getCurrentStage())
                && countTCells() < waveManager.getMaxActiveTCells(cancerManager.getCurrentStage())) {
            spawnTCell();
        }
        maintainHealthyPopulation();
    }

    void updateTCellAggression() {
        float aggression = GameBalanceConfig.getTCellAggressionForSpread(cancerManager.getCurrentSpreadPercent());
        float stageSpeed = GameBalanceConfig.getTCellSpeed(cancerManager.getCurrentStage());
        float healthySpeed = GameBalanceConfig.getHealthyCellSpeed(cancerManager.getCurrentStage());
        float healthyFleeRange = GameBalanceConfig.getHealthyCellFleeRange(cancerManager.getCurrentStage());
        for (Entity entity : scene.getEntities()) {
            if (!entity.isActive()) {
                continue;
            }
            if (entity instanceof TCell) {
                TCell tCell = (TCell) entity;
                tCell.setAggressionLevel(aggression);
                tCell.setBaseSpeed(stageSpeed);
            } else if (entity instanceof NormalCell) {
                ((NormalCell) entity).setMovementProfile(healthySpeed, healthyFleeRange);
            }
        }
    }

    void applyTCellSeparation(float delta) {
        for (Entity entity : scene.getEntities()) {
            if (!(entity instanceof TCell) || !entity.isActive()) {
                continue;
            }

            TCell tCell = (TCell) entity;
            Vector2 center = new Vector2(
                    tCell.getPositionX() + tCell.getWidth() * 0.5f,
                    tCell.getPositionY() + tCell.getHeight() * 0.5f);
            Vector2 separation = new Vector2();

            for (Entity nearby : scene.getNearbyEntities(center.x, center.y, GameBalanceConfig.TCELL_SEPARATION_RADIUS)) {
                if (!(nearby instanceof TCell) || nearby == tCell || !nearby.isActive()) {
                    continue;
                }

                float otherCenterX = nearby.getPositionX() + nearby.getWidth() * 0.5f;
                float otherCenterY = nearby.getPositionY() + nearby.getHeight() * 0.5f;
                float dx = center.x - otherCenterX;
                float dy = center.y - otherCenterY;
                float distSq = dx * dx + dy * dy;
                if (distSq <= 0.0001f) {
                    separation.add((float) (Math.random() - 0.5f), (float) (Math.random() - 0.5f));
                    continue;
                }

                float distance = (float) Math.sqrt(distSq);
                if (distance >= GameBalanceConfig.TCELL_SEPARATION_RADIUS) {
                    continue;
                }

                float weight = (GameBalanceConfig.TCELL_SEPARATION_RADIUS - distance)
                        / GameBalanceConfig.TCELL_SEPARATION_RADIUS;
                separation.add(dx / distance * weight, dy / distance * weight);
            }

            if (separation.len2() > 0f) {
                separation.nor().scl(GameBalanceConfig.TCELL_SEPARATION_STRENGTH * delta);
                tCell.setPosition(tCell.getPositionX() + separation.x, tCell.getPositionY() + separation.y);
            }
        }
    }

    void clampNpcEntities() {
        for (Entity entity : scene.getEntities()) {
            if (entity != player && entity.isActive()) {
                clampToArena(entity);
            }
        }
    }

    void recycleIneffectiveTCells() {
        List<UUID> recycled = new ArrayList<>();
        for (Entity entity : scene.getEntities()) {
            if (!(entity instanceof TCell) || !entity.isActive()) {
                continue;
            }

            TCell tCell = (TCell) entity;
            if (tCell.isPursuingTarget() || !tCell.isIneffectiveForTooLong() || !isNearArenaEdge(tCell)) {
                continue;
            }
            recycled.add(tCell.getId());
        }

        for (UUID id : recycled) {
            scene.removeEntity(id);
        }
        spawnReplacementTCells(recycled.size());
    }

    void removeInactiveEntities() {
        List<UUID> toRemove = new ArrayList<>();
        for (Entity entity : scene.getEntities()) {
            if (entity == player) {
                continue;
            }
            if (!entity.isActive()) {
                toRemove.add(entity.getId());
            } else if (entity instanceof GameEntity && !((GameEntity) entity).isAlive()) {
                toRemove.add(entity.getId());
            }
        }
        for (UUID id : toRemove) {
            scene.removeEntity(id);
        }
    }

    void spawnReplacementTCells(int count) {
        for (int i = 0; i < count; i++) {
            spawnTCell();
        }
    }

    void clampToArena(Entity entity) {
        float x = Math.max(GameBalanceConfig.WORLD_MARGIN,
                Math.min(entity.getPositionX(),
                        GameBalanceConfig.WORLD_WIDTH - GameBalanceConfig.WORLD_MARGIN - entity.getWidth()));
        float y = Math.max(GameBalanceConfig.WORLD_MARGIN,
                Math.min(entity.getPositionY(),
                        GameBalanceConfig.WORLD_HEIGHT - GameBalanceConfig.WORLD_MARGIN - entity.getHeight()));
        entity.setPosition(x, y);
    }

    void drawArenaFrame(OutputManager output, Texture wallTexture) {
        float tileSize = 64f;
        for (float x = 0; x < GameBalanceConfig.WORLD_WIDTH; x += tileSize) {
            output.getBatch().draw(wallTexture, x, 0f, tileSize, tileSize);
            output.getBatch().draw(wallTexture, x, GameBalanceConfig.WORLD_HEIGHT - tileSize, tileSize, tileSize);
        }
        for (float y = tileSize; y < GameBalanceConfig.WORLD_HEIGHT - tileSize; y += tileSize) {
            output.getBatch().draw(wallTexture, 0f, y, tileSize, tileSize);
            output.getBatch().draw(wallTexture, GameBalanceConfig.WORLD_WIDTH - tileSize, y, tileSize, tileSize);
        }
    }

    private void maintainHealthyPopulation() {
        int targetAlive = waveManager.getTargetHealthyCellCount(cancerManager.getCurrentStage());
        int currentAlive = countNormalCells();
        int spawnCount = Math.max(0, targetAlive - currentAlive);
        if (spawnCount <= 0 || player == null) {
            return;
        }

        float speed = GameBalanceConfig.getHealthyCellSpeed(cancerManager.getCurrentStage());
        float fleeRange = GameBalanceConfig.getHealthyCellFleeRange(cancerManager.getCurrentStage());

        for (int i = 0; i < spawnCount; i++) {
            NormalCell cell = CellFactory.createNormalCell(0f, 0f, speed, fleeRange);
            placeEntityAtSpawn(cell, GameBalanceConfig.NORMAL_CELL_MIN_PLAYER_DISTANCE);
            cell.setThreat(player);
            scene.createEntity(cell);
        }
    }

    private void spawnTCell() {
        if (player == null) {
            return;
        }
        TCell tCell = CellFactory.createTCell(0f, 0f, GameBalanceConfig.getTCellSpeed(cancerManager.getCurrentStage()));
        placeEntityAtSpawn(tCell, GameBalanceConfig.TCELL_MIN_PLAYER_DISTANCE);
        tCell.setTarget(player);
        scene.createEntity(tCell);
    }

    private int countNormalCells() {
        return countEntitiesOfType(NormalCell.class);
    }

    private int countTCells() {
        return countEntitiesOfType(TCell.class);
    }

    private int countEntitiesOfType(Class<? extends Entity> type) {
        int count = 0;
        for (Entity entity : scene.getEntities()) {
            if (type.isInstance(entity) && entity.isActive()) {
                count++;
            }
        }
        return count;
    }

    private boolean isNearArenaEdge(Entity entity) {
        float left = entity.getPositionX();
        float right = entity.getPositionX() + entity.getWidth();
        float bottom = entity.getPositionY();
        float top = entity.getPositionY() + entity.getHeight();
        float edgeMargin = GameBalanceConfig.TCELL_RECYCLE_EDGE_MARGIN;
        return left <= GameBalanceConfig.WORLD_MARGIN + edgeMargin
                || right >= GameBalanceConfig.WORLD_WIDTH - GameBalanceConfig.WORLD_MARGIN - edgeMargin
                || bottom <= GameBalanceConfig.WORLD_MARGIN + edgeMargin
                || top >= GameBalanceConfig.WORLD_HEIGHT - GameBalanceConfig.WORLD_MARGIN - edgeMargin;
    }

    private void placeEntityAtSpawn(GameEntity entity, float minDistanceFromPlayer) {
        Vector2 spawn = findSpawnPosition(entity, minDistanceFromPlayer);
        entity.setPosition(spawn.x, spawn.y);
    }

    private Vector2 findSpawnPosition(GameEntity entity, float minDistanceFromPlayer) {
        float maxX = GameBalanceConfig.WORLD_WIDTH - GameBalanceConfig.WORLD_MARGIN - entity.getWidth();
        float maxY = GameBalanceConfig.WORLD_HEIGHT - GameBalanceConfig.WORLD_MARGIN - entity.getHeight();
        float minX = GameBalanceConfig.WORLD_MARGIN;
        float minY = GameBalanceConfig.WORLD_MARGIN;

        for (int attempt = 0; attempt < GameBalanceConfig.SPAWN_ATTEMPTS; attempt++) {
            float x = minX + (float) (Math.random() * Math.max(1f, maxX - minX));
            float y = minY + (float) (Math.random() * Math.max(1f, maxY - minY));
            entity.setPosition(x, y);
            if (isSpawnPositionValid(entity, minDistanceFromPlayer)) {
                return new Vector2(x, y);
            }
        }

        float fallbackX = Math.max(minX, Math.min(entity.getPositionX(), maxX));
        float fallbackY = Math.max(minY, Math.min(entity.getPositionY(), maxY));
        return new Vector2(fallbackX, fallbackY);
    }

    private boolean isSpawnPositionValid(GameEntity entity, float minDistanceFromPlayer) {
        Rectangle bounds = entity.getBounds();
        Rectangle queryArea = new Rectangle(
                bounds.x - GameBalanceConfig.SPAWN_PADDING,
                bounds.y - GameBalanceConfig.SPAWN_PADDING,
                bounds.width + GameBalanceConfig.SPAWN_PADDING * 2f,
                bounds.height + GameBalanceConfig.SPAWN_PADDING * 2f);

        if (player != null) {
            Rectangle playerBounds = player.getBounds();
            if (queryArea.overlaps(playerBounds)) {
                return false;
            }
            float dx = (bounds.x + bounds.width * 0.5f) - (playerBounds.x + playerBounds.width * 0.5f);
            float dy = (bounds.y + bounds.height * 0.5f) - (playerBounds.y + playerBounds.height * 0.5f);
            if (dx * dx + dy * dy < minDistanceFromPlayer * minDistanceFromPlayer) {
                return false;
            }
        }

        Collection<Entity> existingEntities = scene.getEntitiesInBounds(queryArea);
        for (Entity existing : existingEntities) {
            if (!(existing instanceof GameEntity) || !existing.isActive()) {
                continue;
            }
            if (((GameEntity) existing).getBounds().overlaps(queryArea)) {
                return false;
            }
        }

        return true;
    }
}
