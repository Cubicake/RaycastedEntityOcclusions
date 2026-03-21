package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.locatables.block.BlockLocatable;
import games.cubi.raycastedAntiESP.utils.ConcurrentSetMap;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TileEntityCache {
    private final AtomicReference<ConcurrentSetMap<UUID, BlockLocatable>> tileEntitiesToShowCache = new AtomicReference<>(new ConcurrentSetMap<>());

    protected AtomicReference<ConcurrentSetMap<UUID, BlockLocatable>> tileEntitiesToShowCacheAtomicReference() {
        return tileEntitiesToShowCache;
    }

    protected ConcurrentSetMap<UUID, BlockLocatable> entitiesToShowCache() {
        return tileEntitiesToShowCache.get();
    }

    protected void addToTileEntityCache(UUID player, BlockLocatable value) {
        entitiesToShowCache().add(player, value);
    }

    protected Set<BlockLocatable> getFromTileEntityCache(UUID key) {
        return entitiesToShowCache().getOrDefault(key, Set.of());
    }

    protected Map<UUID, Set<BlockLocatable>> flushTileEntityShowCache() {
        return tileEntitiesToShowCache.getAndSet(new ConcurrentSetMap<>()).asActualConcurrentMap();
    }
}
