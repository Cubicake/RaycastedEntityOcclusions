package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.entity.EntityVisibilityChanger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BukkitAbstractVisibilityChanger extends AbstractEntityCache { //abstracted because entity and player logic is identical

    /**
     * @param playerUUID
     * @param entityUUID While player UUIDs will be accepted since this method does not use instanceof checks for performance reasons, you must only pass non-player entities here.
     */
    //TODO: Find out if performance is actually a concern here, if not we can do instanceof checks to prevent misuse
    public void showAbstractEntityToPlayer(UUID playerUUID, UUID entityUUID) {
        if (DataHolder.players().getPlayerData(playerUUID).compareAndSetEntityVisibility(entityUUID, true)) {
            addEntityToShowCache(playerUUID, entityUUID); // Only process entity visibility changes if there was an actual change
        }
    }

    public void hideAbstractEntityFromPlayer(UUID playerUUID, UUID entityUUID) {
        if (DataHolder.players().getPlayerData(playerUUID).compareAndSetEntityVisibility(entityUUID, false)) {
            addEntityToHideCache(playerUUID, entityUUID);
        }
    }

    /**
     * Only call from main thread!
     */
    public void processCaches() {
        Map<UUID, Set<UUID>> showCache = flushShowCache();
        for (var entry : showCache.entrySet()) {
            UUID playerUUID = entry.getKey();
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            for (UUID entityUUID : entry.getValue()) {
                Entity entity = Bukkit.getEntity(entityUUID);
                if (entity == null) return;
                player.showEntity(RaycastedAntiESP.get(), entity);
            }
        }

        Map<UUID, Set<UUID>> hideCache = flushHideCache();
        for (var entry : hideCache.entrySet()) {
            UUID playerUUID = entry.getKey();
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;

            for (UUID entityUUID : entry.getValue()) {
                Entity entity = Bukkit.getEntity(entityUUID);
                if (entity != null) {
                    player.hideEntity(RaycastedAntiESP.get(), entity);
                }
            }
        }
    }
}
