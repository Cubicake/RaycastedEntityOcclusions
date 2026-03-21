package games.cubi.raycastedantiesp.paper.visibilitychangehandlers.entity;

import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.VisibilityChangeHandlers;

import java.util.UUID;

public interface EntityVisibilityChanger {
    //Implementation detail: Remember to update the playerdata visibility maps when changing entity visibility
    void showEntityToPlayer(UUID player, UUID entity, int currentTick);
    void hideEntityFromPlayer(UUID player, UUID entity, int currentTick);
    default void setEntityVisibilityForPlayer(UUID player, UUID entity, boolean visible, int currentTick) {
        if (visible) {
            showEntityToPlayer(player, entity, currentTick);
        } else {
            hideEntityFromPlayer(player, entity, currentTick);
        }
    }

    VisibilityChangeHandlers.EntityVisibilityChangerType getType();

    void processCache();

    //TODO: Impl note: Check that the visibility state differs before applying changes to avoid redundant operations
}
