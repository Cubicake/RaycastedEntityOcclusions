package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import java.util.UUID;

public interface EntityVisibilityChanger {
    void showEntityToPlayer(UUID player, UUID entity);
    void hideEntityFromPlayer(UUID player, UUID entity);
    default void setEntityVisibilityForPlayer(UUID player, UUID entity, boolean visible) {
        if (visible) {
            showEntityToPlayer(player, entity);
        } else {
            hideEntityFromPlayer(player, entity);
        }
    }

    //TODO: Impl note: Check that the visibility state differs before applying changes to avoid redundant operations
}
