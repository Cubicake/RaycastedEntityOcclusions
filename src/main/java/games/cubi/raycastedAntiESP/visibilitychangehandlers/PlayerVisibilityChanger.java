package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import java.util.UUID;

public interface PlayerVisibilityChanger {
    // Not sure if this will be kept distinct from entity handling
    // Future packet shenanigans may need this

    /**
     * First UUID is player receiving visibility change, second is the player which will become visible
     * @param player
     * @param otherPlayer
     */
    void showPlayerToPlayer(UUID player, UUID otherPlayer);
    /**
     * First UUID is player receiving visibility change, second is the player which will become hidden
     * @param player
     * @param otherPlayer
     */
    void hidePlayerFromPlayer(UUID player, UUID otherPlayer);

    /**
     * First UUID is player receiving visibility change, second is the player which will become hidden
     * @param player
     * @param otherPlayer
     */
    default void setPlayerVisibilityForPlayer(UUID player, UUID otherPlayer, boolean visible) {
        if (visible) {
            showPlayerToPlayer(player, otherPlayer);
        } else {
            hidePlayerFromPlayer(player, otherPlayer);
        }
    }
}
