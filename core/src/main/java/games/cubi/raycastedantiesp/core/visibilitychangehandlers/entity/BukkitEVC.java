package games.cubi.raycastedantiesp.core.visibilitychangehandlers.entity;

import games.cubi.raycastedantiesp.core.Logger;
import games.cubi.raycastedantiesp.core.data.DataHolder;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public class BukkitEVC extends BukkitAbstractVisibilityChanger implements EntityVisibilityChanger {

    @Override
    public void showEntityToPlayer(UUID player, UUID entity, int currentTick) {
        if (!DataHolder.players().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3);
            return;
        }
        super.showAbstractEntityToPlayer(player, entity, currentTick);
    }

    @Override
    public void hideEntityFromPlayer(UUID player, UUID entity, int currentTick) {
        if (!DataHolder.players().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3);
            return;
        }
        super.hideAbstractEntityFromPlayer(player, entity, currentTick);
    }

    @Override
    public VisibilityChangeHandlers.EntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT;
    }

    @Override
    public void processCache() {
        processCaches();
    }
}
