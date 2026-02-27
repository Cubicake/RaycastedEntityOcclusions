package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.utils.ConcurrentSetMap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TileEntityCache {
    private final AtomicReference<ConcurrentSetMap<UUID, AbstractBlockLocation>> tileEntitiesToShowCache = new AtomicReference<>(new ConcurrentSetMap<>());

    protected AtomicReference<ConcurrentSetMap<UUID, AbstractBlockLocation>> tileEntitiesToShowCacheAtomicReference() {
        return tileEntitiesToShowCache;
    }

    protected ConcurrentSetMap<UUID, AbstractBlockLocation> entitiesToShowCache() {
        return tileEntitiesToShowCache.get();
    }

    protected void addToTileEntityCache(UUID player, AbstractBlockLocation value) {
        entitiesToShowCache().add(player, value);
    }

    protected Set<AbstractBlockLocation> getFromTileEntityCache(UUID key) {
        return entitiesToShowCache().getOrDefault(key, Set.of());
    }

    protected Map<UUID, Set<AbstractBlockLocation>> flushTileEntityShowCache() {
        return tileEntitiesToShowCache.getAndSet(new ConcurrentSetMap<>()).asActualConcurrentMap();
    }
}
