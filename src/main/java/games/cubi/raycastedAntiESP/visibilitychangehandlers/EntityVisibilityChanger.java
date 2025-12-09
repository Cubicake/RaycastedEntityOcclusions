package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import java.util.UUID;

public interface EntityVisibilityChanger {
    void showEntityToPlayer(UUID player, UUID entity);
    void hideEntityFromPlayer(UUID player, UUID entity);
}
