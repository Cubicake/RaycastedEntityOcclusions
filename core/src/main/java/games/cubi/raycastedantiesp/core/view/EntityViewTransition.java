package games.cubi.raycastedantiesp.core.view;

import java.util.UUID;

// Used to cache visibility changes until the player's netty thread next processes
public record EntityViewTransition(Type type, UUID targetUUID, int entityID) {
    public enum Type {
        SHOW,
        HIDE,
        FORGET,
    }
}
