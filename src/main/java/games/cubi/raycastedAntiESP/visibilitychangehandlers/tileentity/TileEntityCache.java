package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TileEntityCache {
    private final AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToShowCache = new AtomicReference<>(new ConcurrentHashMap<>());
    private final AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToHideCache = new AtomicReference<>(new ConcurrentHashMap<>());

    protected AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToShowCacheAtomicReference() {
        return tileEntitiesToShowCache;
    }

    protected AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToHideCacheAtomicReference() {
        return tileEntitiesToHideCache;
    }

    protected ConcurrentHashMap<UUID, Set<AbstractBlockLocation>> entitiesToShowCache() {
        return tileEntitiesToShowCache.get();
    }
    protected ConcurrentHashMap<UUID, Set<AbstractBlockLocation>> tileEntitiesToHideCache() {
        return tileEntitiesToHideCache.get();
    }

    private ConcurrentHashMap<UUID, Set<AbstractBlockLocation>> getCache(boolean visibility) {
        return visibility ? entitiesToShowCache() : tileEntitiesToHideCache();
    }

    protected void addToTileEntityCache(UUID player, AbstractBlockLocation value, boolean visibility) {
        getCache(visibility).computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet())
                .add(value);
    }

    protected Set<AbstractBlockLocation> getFromTileEntityCache(UUID key, boolean visibility) {
        return getCache(visibility).getOrDefault(key, Set.of());
    }

    protected Map<UUID, Set<AbstractBlockLocation>> flushTileEntityShowCache() {
        return tileEntitiesToShowCache.getAndSet(new ConcurrentHashMap<>());
    }

    protected Map<UUID, Set<AbstractBlockLocation>> flushTileEntityHideCache() {
        return tileEntitiesToHideCache.getAndSet(new ConcurrentHashMap<>());
    }
}
