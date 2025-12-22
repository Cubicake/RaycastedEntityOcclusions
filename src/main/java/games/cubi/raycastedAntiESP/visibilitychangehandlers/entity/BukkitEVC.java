package games.cubi.raycastedAntiESP.visibilitychangehandlers.entity;

import games.cubi.raycastedAntiESP.visibilitychangehandlers.BukkitAbstractVisibilityChanger;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public class BukkitEVC extends BukkitAbstractVisibilityChanger implements EntityVisibilityChanger {

    @Override
    public void showEntityToPlayer(UUID player, UUID entity) {
        super.showAbstractEntityToPlayer(player, entity);
    }

    @Override
    public void hideEntityFromPlayer(UUID player, UUID entity) {
        super.hideAbstractEntityFromPlayer(player, entity);
    }

    @Override
    public VisibilityChangeHandlers.EntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT;
    }
}
