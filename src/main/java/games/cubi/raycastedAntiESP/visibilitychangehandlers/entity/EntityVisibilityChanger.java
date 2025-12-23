package games.cubi.raycastedAntiESP.visibilitychangehandlers.entity;

import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public interface EntityVisibilityChanger {
    //Implementation detail: Remember to update the playerdata visibility maps when changing entity visibility
    void showEntityToPlayer(UUID player, UUID entity);
    void hideEntityFromPlayer(UUID player, UUID entity);
    default void setEntityVisibilityForPlayer(UUID player, UUID entity, boolean visible) {
        if (visible) {
            showEntityToPlayer(player, entity);
        } else {
            hideEntityFromPlayer(player, entity);
        }
    }

    VisibilityChangeHandlers.EntityVisibilityChangerType getType();

    void processCache();

    //TODO: Impl note: Check that the visibility state differs before applying changes to avoid redundant operations
}
