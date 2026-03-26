package io.github.some_example_name.game.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.github.some_example_name.engine.entity.Entity;
import io.github.some_example_name.game.entity.CancerCell;
import io.github.some_example_name.game.entity.GameEntity;
import io.github.some_example_name.game.entity.NormalCell;
import io.github.some_example_name.game.entity.TCell;
import io.github.some_example_name.game.io.CellAudioHandler;
import io.github.some_example_name.game.util.CancerEvolutionManager;

public final class GameInteractionSystem {
    private static final float DEFAULT_DAMAGE = 18f;
    private static final float HEALTHY_CELL_HEAL = 5f;

    public static final class InteractionResult {
        private final List<UUID> removedEntityIds = new ArrayList<>();
        private boolean playerKilled;
        private int infectedCount;
        private int replacementTCellCount;

        public List<UUID> getRemovedEntityIds() {
            return removedEntityIds;
        }

        public boolean isPlayerKilled() {
            return playerKilled;
        }

        public int getInfectedCount() {
            return infectedCount;
        }

        public int getReplacementTCellCount() {
            return replacementTCellCount;
        }
    }

    public InteractionResult process(CancerCell player, Collection<Entity> nearbyEntities,
                                     CancerEvolutionManager cancerManager, CellAudioHandler audioHandler,
                                     float spreadPerCell) {
        InteractionResult result = new InteractionResult();
        if (player == null || nearbyEntities == null) {
            return result;
        }

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof GameEntity) || entity == player || !entity.isActive()) {
                continue;
            }

            GameEntity other = (GameEntity) entity;
            if (!player.overlaps(other)) {
                continue;
            }

            if (other instanceof NormalCell) {
                other.setActive(false);
                result.getRemovedEntityIds().add(other.getId());
                result.infectedCount++;
                player.gainExp(50f);
                player.heal(HEALTHY_CELL_HEAL);
                cancerManager.addSpread(spreadPerCell);
                if (audioHandler != null) {
                    audioHandler.playEatCellSquelch();
                }
            } else if (other instanceof TCell) {
                other.setActive(false);
                result.getRemovedEntityIds().add(other.getId());
                result.replacementTCellCount++;
                player.takeDamage(DEFAULT_DAMAGE);
                if (audioHandler != null) {
                    audioHandler.playTCellDamage();
                }
                result.playerKilled = player.getHp() <= 0f;
            }
        }

        return result;
    }
}
