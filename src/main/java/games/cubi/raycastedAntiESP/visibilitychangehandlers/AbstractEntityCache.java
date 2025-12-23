package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractEntityCache {

    private final AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToShowCache = new AtomicReference<>(new ConcurrentHashMap<>());
    private final AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToHideCache = new AtomicReference<>(new ConcurrentHashMap<>());

    protected AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToShowCacheAtomicReference() {
        return entitiesToShowCache;
    }

    protected AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> entitiesToHideCacheAtomicReference() {
        return entitiesToHideCache;
    }

    protected ConcurrentHashMap<UUID, Set<UUID>> entitiesToShowCache() {
        return entitiesToShowCacheAtomicReference().get();
    }
    protected ConcurrentHashMap<UUID, Set<UUID>> entitiesToHideCache() {
        return entitiesToHideCacheAtomicReference().get();
    }

    private void addGeneric(UUID key, UUID value, ConcurrentHashMap<UUID, Set<UUID>> map) {
        map.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                .add(value);
    }

    private Set<UUID> getGeneric(UUID key, ConcurrentHashMap<UUID, Set<UUID>> map) {
        return map.getOrDefault(key, Set.of());
    }

    private Map<UUID, Set<UUID>> flushGeneric(AtomicReference<ConcurrentHashMap<UUID, Set<UUID>>> reference) {
        return reference.getAndSet(new ConcurrentHashMap<>());
    }

    protected Map<UUID, Set<UUID>> flushShowCache() {
        return flushGeneric(entitiesToShowCacheAtomicReference());
    }

    protected Map<UUID, Set<UUID>> flushHideCache() {
        return flushGeneric(entitiesToHideCacheAtomicReference());
    }

    protected void addEntityToShowCache(UUID player, UUID entity) {
        addGeneric(player, entity, entitiesToShowCache());
    }

    protected void addEntityToHideCache(UUID player, UUID entity) {
        addGeneric(player, entity, entitiesToHideCache());
    }

    protected Set<UUID> getEntitiesToShowFromCache(UUID player) {
        return getGeneric(player, entitiesToShowCache());
    }

    protected Set<UUID> getEntitiesToHideFromCache(UUID player) {
        return getGeneric(player, entitiesToHideCache());
    }

    protected Map<UUID, Set<UUID>> flushEntitiesToShowCache() {
        return flushGeneric(entitiesToShowCacheAtomicReference());
    }

    protected Map<UUID, Set<UUID>> flushEntitiesToHideCache() {
        return flushGeneric(entitiesToHideCacheAtomicReference());
    }
}
