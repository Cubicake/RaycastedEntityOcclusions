package games.cubi.raycastedAntiESP.visibilitychangehandlers.entity;

import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class BukkitEVC implements EntityVisibilityChanger{

    private final AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToShowCache = new AtomicReference<>(new ConcurrentHashMap<>());
    private final AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToHideCache = new AtomicReference<>(new ConcurrentHashMap<>());

    /**
     * @param playerUUID
     * @param entityUUID While player UUIDs will be accepted since this method does not use instanceof checks for performance reasons, you must only pass non-player entities here.
     */
    //TODO: Find out if performance is actually a concern here, if not we can do instanceof checks to prevent misuse
    @Override
    public void showEntityToPlayer(UUID playerUUID, UUID entityUUID) {
        if (DataHolder.players().getPlayerData(playerUUID).compareAndSetEntityVisibility(entityUUID, true)) {
            addEntityToShowCache(playerUUID, entityUUID); // Only process entity visibility changes if there was an actual change
        }
    }

    @Override
    public void hideEntityFromPlayer(UUID playerUUID, UUID entityUUID) {
        if (DataHolder.players().getPlayerData(playerUUID).compareAndSetEntityVisibility(entityUUID, false)) {
            addEntityToHideCache(playerUUID, entityUUID);
        }
    }

    @Override
    public VisibilityChangeHandlers.EntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.EntityVisibilityChangerType.BUKKIT;
    }

    @Override
    public AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToShowCacheAtomicReference() {
        return entitiesToShowCache;
    }

    @Override
    public AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToHideCacheAtomicReference() {
        return entitiesToHideCache;
    }

    /**
     * Only call from main thread!
     */
    public void processCaches() {
        Map<UUID, Set<UUID>> showCache = flushGeneric(entitiesToShowCacheAtomicReference());
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

        Map<UUID, Set<UUID>> hideCache = flushGeneric(entitiesToHideCacheAtomicReference());
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
