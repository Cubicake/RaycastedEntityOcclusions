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

    //TODO: Impl note: Check that the visibility state differs before applying changes to avoid redundant operations

    AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToShowCacheAtomicReference();
    AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToHideCacheAtomicReference();

    default ConcurrentHashMap<UUID, Set<UUID>> entitiesToShowCache() {
        return entitiesToShowCacheAtomicReference().get();
    }
    default ConcurrentHashMap<UUID, Set<UUID>> entitiesToHideCache() {
        return entitiesToHideCacheAtomicReference().get();
    }

    default void addGeneric(UUID key, UUID value, ConcurrentHashMap<UUID, Set<UUID>> map) {
        map.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                .add(value);
    }

    default Set<UUID> getGeneric(UUID key, ConcurrentHashMap<UUID, Set<UUID>> map) {
        return map.getOrDefault(key, Set.of());
    }

    default Map<UUID, Set<UUID>> flushGeneric(AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> reference) {
        return reference.getAndSet(new ConcurrentHashMap<>());
    }

    default void addEntityToShowCache(UUID player, UUID entity) {
        addGeneric(player, entity, entitiesToShowCache());
    }

    default void addEntityToHideCache(UUID player, UUID entity) {
        addGeneric(player, entity, entitiesToHideCache());
    }

    default Set<UUID> getEntitiesToShowFromCache(UUID player) {
        return getGeneric(player, entitiesToShowCache());
    }

    default Set<UUID> getEntitiesToHideFromCache(UUID player) {
        return getGeneric(player, entitiesToHideCache());
    }

    default Map<UUID, Set<UUID>> flushEntitiesToShowCache() {
        return flushGeneric(entitiesToShowCacheAtomicReference());
    }

    default Map<UUID, Set<UUID>> flushEntitiesToHideCache() {
        return flushGeneric(entitiesToHideCacheAtomicReference());
    }
}
