package games.cubi.raycastedAntiESP.visibilitychangehandlers;

import games.cubi.raycastedAntiESP.utils.ConcurrentSetMap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractEntityCache {

    private final AtomicReference<ConcurrentSetMap<UUID, UUID>> entitiesToShowCache = new AtomicReference<>(new ConcurrentSetMap<>());
    private final AtomicReference<ConcurrentSetMap<UUID, UUID>> entitiesToHideCache = new AtomicReference<>(new ConcurrentSetMap<>());

    protected AtomicReference<ConcurrentSetMap<UUID, UUID>> entitiesToShowCacheAtomicReference() {
        return entitiesToShowCache;
    }

    protected AtomicReference<ConcurrentSetMap<UUID, UUID>> entitiesToHideCacheAtomicReference() {
        return entitiesToHideCache;
    }

    protected ConcurrentSetMap<UUID, UUID> entitiesToShowCache() {
        return entitiesToShowCacheAtomicReference().get();
    }
    protected ConcurrentSetMap<UUID, UUID> entitiesToHideCache() {
        return entitiesToHideCacheAtomicReference().get();
    }

    private void addGeneric(UUID key, UUID value, ConcurrentSetMap<UUID, UUID> map) {
        map.add(key, value);
    }

    private Set<UUID> getGeneric(UUID key, ConcurrentSetMap<UUID, UUID> map) {
        return map.getOrDefault(key, Set.of());
    }

    private Map<UUID, Set<UUID>> flushGeneric(AtomicReference<ConcurrentSetMap<UUID, UUID>> reference) {
        return reference.getAndSet(new ConcurrentSetMap<>()).asActualConcurrentMap(); // Map is dereferenced after this so it is safe to return the actual map, and this avoids an unnecessary copy of the data.
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
}
