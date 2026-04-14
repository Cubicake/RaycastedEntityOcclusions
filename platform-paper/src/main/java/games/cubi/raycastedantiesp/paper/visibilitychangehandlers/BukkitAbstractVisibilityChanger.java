package games.cubi.raycastedantiesp.paper.visibilitychangehandlers;

import games.cubi.raycastedantiesp.core.players.VisibilityTracker;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class BukkitAbstractVisibilityChanger extends AbstractEntityCache implements Listener { //abstracted because entity and player logic is identical

    public BukkitAbstractVisibilityChanger() {
        super();
        Bukkit.getPluginManager().registerEvents(this, RaycastedAntiESP.get());
    }

    protected abstract VisibilityTracker<UUID> getCorrectVisibilityTracker(UUID playerUUID);

    /**
     * @param playerUUID
     * @param entityUUID While player UUIDs will be accepted since this method does not use instanceof checks for performance reasons, you must only pass non-player entities here.
     */
    //TODO: Find out if performance is actually a concern here, if not we can do instanceof checks to prevent misuse
    public void showAbstractEntityToPlayer(UUID playerUUID, UUID entityUUID, int currentTick) {
        if (getCorrectVisibilityTracker(playerUUID).compareAndSetVisibility(entityUUID, true, currentTick)) {
            addEntityToShowCache(playerUUID, entityUUID); // Only process entity visibility changes if there was an actual change
        }
    }

    public void hideAbstractEntityFromPlayer(UUID playerUUID, UUID entityUUID, int currentTick) {
        if (getCorrectVisibilityTracker(playerUUID).compareAndSetVisibility(entityUUID, false, currentTick)) {
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
