package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TileEntityCache {
    private final AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToShowCache = new AtomicReference<>(new ConcurrentHashMap<>());

    protected AtomicReference<ConcurrentHashMap<UUID, Set<AbstractBlockLocation>>> tileEntitiesToShowCacheAtomicReference() {
        return tileEntitiesToShowCache;
    }

    protected ConcurrentHashMap<UUID, Set<AbstractBlockLocation>> entitiesToShowCache() {
        return tileEntitiesToShowCache.get();
    }

    private ConcurrentHashMap<UUID, Set<AbstractBlockLocation>> getCache() {
        return entitiesToShowCache();
    }

    protected void addToTileEntityCache(UUID player, AbstractBlockLocation value) {
        getCache().computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet())
                .add(value);
    }

    protected Set<AbstractBlockLocation> getFromTileEntityCache(UUID key) {
        return getCache().getOrDefault(key, Set.of());
    }

    protected Map<UUID, Set<AbstractBlockLocation>> flushTileEntityShowCache() {
        return tileEntitiesToShowCache.getAndSet(new ConcurrentHashMap<>());
    }
}
