package games.cubi.raycastedAntiESP.visibilitychangehandlers.entity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.utils.PlayerData;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public class BukkitEVC extends BukkitAbstractVisibilityChanger implements EntityVisibilityChanger {

    @Override
    public void showEntityToPlayer(UUID player, UUID entity) {
        PlayerData data = DataHolder.players().getPlayerData(player);
        if (data == null) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3);
            return;
        }
        super.showAbstractEntityToPlayer(player, entity);
    }

    @Override
    public void hideEntityFromPlayer(UUID player, UUID entity) {
        PlayerData data = DataHolder.players().getPlayerData(player);
        if (data == null) {
            Logger.errorAndReturn(new RuntimeException("Null PlayerData when attempting to show entity to player"), 3);
            return;
        }
        super.hideAbstractEntityFromPlayer(player, entity);
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
