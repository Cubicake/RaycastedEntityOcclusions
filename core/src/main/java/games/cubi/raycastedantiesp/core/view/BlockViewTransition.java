package games.cubi.raycastedantiesp.core.view;

import games.cubi.locatables.BlockLocatable;

// Used to cache visibility changes until the player's netty thread next processes them.
public record BlockViewTransition(Type type, BlockLocatable location) {
    public enum Type {
        SHOW,
        HIDE,
    }
}
