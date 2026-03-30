package games.cubi.raycastedantiesp.paper.visibilitychangehandlers.player;

import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.player.PlayerVisibilityChanger;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.BukkitAbstractVisibilityChanger;

import java.util.UUID;

public class BukkitPVC extends BukkitAbstractVisibilityChanger implements PlayerVisibilityChanger{
    @Override
    public void showPlayerToPlayer(UUID player, UUID otherPlayer, int currentTick) {
        super.showAbstractEntityToPlayer(player, otherPlayer, currentTick);
    }

    @Override
    public void hidePlayerFromPlayer(UUID player, UUID otherPlayer, int currentTick) {
        super.hideAbstractEntityFromPlayer(player, otherPlayer, currentTick);
    }

    @Override
    public void processCache() {
        processCaches();
    }

    @Override
    public VisibilityChangeHandlers.PlayerVisibilityChangerType getType() {
        return VisibilityChangeHandlers.PlayerVisibilityChangerType.BUKKIT;
    }
}
