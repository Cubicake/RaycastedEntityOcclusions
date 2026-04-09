package games.cubi.raycastedantiesp.paper.visibilitychangehandlers.entity;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.EntityVisibilityChanger;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.BukkitAbstractVisibilityChanger;

import java.util.UUID;

public class BukkitEVC extends BukkitAbstractVisibilityChanger implements EntityVisibilityChanger {

    @Override
    public void showEntityToPlayer(UUID player, UUID entity, int currentTick) {
        if (!PlayerRegistry.getInstance().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3, BukkitEVC.class);
            return;
        }
        super.showAbstractEntityToPlayer(player, entity, currentTick);
    }

    @Override
    public void hideEntityFromPlayer(UUID player, UUID entity, int currentTick) {
        if (!PlayerRegistry.getInstance().isPlayerRegistered(player)) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3, BukkitEVC.class);
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
