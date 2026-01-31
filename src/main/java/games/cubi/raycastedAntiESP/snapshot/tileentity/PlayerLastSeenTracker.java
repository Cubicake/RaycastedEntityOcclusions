package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import net.kyori.adventure.util.TriState;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PlayerLastSeenTracker implements TileEntitySnapshotManager {
    private final ConcurrentHashMap<BlockLocation, Set<PlayerLastCheckTimestamp>> tileEntityLastSeenMap = new ConcurrentHashMap<>();

    public Set<PlayerLastCheckTimestamp> getPlayerLastSeenTimestamps(AbstractBlockLocation location) {
        Set<PlayerLastCheckTimestamp> set = tileEntityLastSeenMap.get(location);
        return set == null ? Set.of() : Set.copyOf(set);
    }

    public boolean tileEntityLastSeenMapContains(AbstractBlockLocation location) {
        return tileEntityLastSeenMap.containsKey(location);
    }

    public TriState canPlayerSeeTileEntity(UUID playerUUID, BlockLocation location) {
        Set<PlayerLastCheckTimestamp> set = tileEntityLastSeenMap.get(location); //not atomic, but 100% guarantee is not necessary here
        if (set != null) {
            for (PlayerLastCheckTimestamp entry : set) {
                if (entry.getPlayer().equals(playerUUID)) {
                    return TriState.byBoolean(entry.hasBeenSeen());
                }
            }
        }
        return TriState.NOT_SET;
    }

    public void setValuesInTileEntityLastSeenMap(BlockLocation location, Set<PlayerLastCheckTimestamp> values) {
        Set<PlayerLastCheckTimestamp> newSet = ConcurrentHashMap.newKeySet();
        newSet.addAll(values);
        tileEntityLastSeenMap.put(location, newSet);
    }

    public void removeFromTileEntityLastSeenMap(AbstractBlockLocation location) {
        tileEntityLastSeenMap.remove(location);
    }

    public void markAsNotVisible(BlockLocation location, UUID playerUUID) {
        addOrUpdateTileEntityLastSeenMap(location, playerUUID, false);
    }

    public TriState isTileEntityVisibleToPlayer(BlockLocation location, UUID playerUUID) {

        AtomicReference<TriState> result = new AtomicReference<>();

        Set<PlayerLastCheckTimestamp> playerLastCheckTimestampSet = tileEntityLastSeenMap.compute(location, (key, set) -> {
            if (set == null) {
                Set<PlayerLastCheckTimestamp> newSet = ConcurrentHashMap.newKeySet();
                newSet.add(new PlayerLastCheckTimestamp(playerUUID, DataHolder.getTick(), false));
                result.set(TriState.NOT_SET);
                return newSet;
            }
            // Check existing entry
            for (PlayerLastCheckTimestamp playerLastCheckTimestamp : set) {
                if (playerLastCheckTimestamp.getPlayer().equals(playerUUID)) {
                    result.set(playerLastCheckTimestamp.hasBeenSeen() ? TriState.TRUE : TriState.FALSE);
                    return set;
                }
            }
            // Otherwise add new entry
            set.add(new PlayerLastCheckTimestamp(playerUUID, DataHolder.getTick(), false));
            result.set(TriState.NOT_SET);
            return set;
        });

        return result.get();
    }

    public void addOrUpdateTileEntityLastSeenMap(BlockLocation location, UUID playerUUID, boolean visible) {
        //mutate existing UUID entry if it exists, otherwise add new
        int timestamp = DataHolder.getTick();
        tileEntityLastSeenMap.compute(location, (key, set) -> {
            if (set == null) {
                Set<PlayerLastCheckTimestamp> newSet = ConcurrentHashMap.newKeySet();
                newSet.add(new PlayerLastCheckTimestamp(playerUUID, timestamp, visible));
                return newSet;
            }
            // Mutate existing entry if it exists
            for (PlayerLastCheckTimestamp playerLastCheckTimestamp : set) {
                if (playerLastCheckTimestamp.getPlayer().equals(playerUUID)) {
                    playerLastCheckTimestamp.update(timestamp, visible); //this may not be atomic/thread-safe, but it's unlikely to cause issues in practice
                    return set;
                }
            }
            // Otherwise add new entry
            set.add(new PlayerLastCheckTimestamp(playerUUID, timestamp, visible));
            return set;
        });
    }

    public void clearTileEntityLastSeenMap() {
        tileEntityLastSeenMap.clear();
    }
}
