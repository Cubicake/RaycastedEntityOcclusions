package games.cubi.raycastedAntiESP.visibilitychangehandlers.player;

import games.cubi.raycastedAntiESP.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public class BukkitPVC extends BukkitAbstractVisibilityChanger implements PlayerVisibilityChanger{
    @Override
    public void showPlayerToPlayer(UUID player, UUID otherPlayer) {
        super.showAbstractEntityToPlayer(player, otherPlayer);
    }

    @Override
    public void hidePlayerFromPlayer(UUID player, UUID otherPlayer) {
        super.hideAbstractEntityFromPlayer(player, otherPlayer);
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
