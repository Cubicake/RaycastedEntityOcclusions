package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerLastSeenTracker implements TileEntitySnapshotManager {
    private final ConcurrentHashMap<BlockLocation, Set<PlayerLastCheckTimestamp>> tileEntityLastSeenMap = new ConcurrentHashMap<>();

    public Set<PlayerLastCheckTimestamp> getPlayerLastSeenTimestamps(AbstractBlockLocation location) {
        Set<PlayerLastCheckTimestamp> set = tileEntityLastSeenMap.get(location);
        return set == null ? Set.of() : Set.copyOf(set);
    }

    public boolean tileEntityLastSeenMapContains(AbstractBlockLocation location) {
        return tileEntityLastSeenMap.containsKey(location);
    }

    public void setValuesInTileEntityLastSeenMap(BlockLocation location, Set<PlayerLastCheckTimestamp> values) {
        Set<PlayerLastCheckTimestamp> newSet = ConcurrentHashMap.newKeySet();
        newSet.addAll(values);
        tileEntityLastSeenMap.put(location, newSet);
    }

    public void removeFromTileEntityLastSeenMap(AbstractBlockLocation location) {
        tileEntityLastSeenMap.remove(location);
    }

    public boolean isTileEntityVisibleToPlayer(BlockLocation location, UUID playerUUID) {
        Set<PlayerLastCheckTimestamp> set = tileEntityLastSeenMap.get(location);
        if (set == null) return false;
        for (PlayerLastCheckTimestamp playerLastCheckTimestamp : set) {
            if (playerLastCheckTimestamp.getPlayer().equals(playerUUID)) {
                return playerLastCheckTimestamp.hasBeenSeen();
            }
        }
        return false; //default to not visible if no entry exists
    }

    public void addOrUpdateTileEntityLastSeenMap(BlockLocation location, UUID playerUUID, int timestamp, boolean visible) {
        //mutate existing UUID entry if it exists, otherwise add new
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
