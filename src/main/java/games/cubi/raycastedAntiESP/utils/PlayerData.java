package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.data.DataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerData {

    private record EntityVisibilityAndLastCheckTime(boolean visible, int lastChecked) {
        public EntityVisibilityAndLastCheckTime(boolean visible) {
            this(visible, DataHolder.getTick());
        }
    }

    // Maps must be thread-safe as their values will be updated while async engine jobs are running, but UUID is probably fine.
    private final UUID playerUUID;
    private boolean hasBypassPermission;

    // UUID = Entity UUID, Boolean = if it is visible to the player. False = hidden
    private final ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> entityVisibility = new ConcurrentHashMap<>();

    // Location, time in millis TODO: Is it necessary to store the time as a long? Do we even need a recheck interval at all?
    private final ConcurrentHashMap<BlockLocation, Long> seenTileEntities = new ConcurrentHashMap<>();

    public PlayerData(UUID playerUUID, boolean hasBypassPermission) {
        this.playerUUID = playerUUID;
        this.hasBypassPermission = hasBypassPermission;
    }

    public PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.hasBypassPermission = player.hasPermission("raycastedentityocclusions.bypass");
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Adds the provided set of entity UUIDs to the entity visibility map with a default visibility of false (not visible). If an entity UUID already exists in the map, it will not be modified.
     * **/
    public void addEntities(Set<UUID> entityUUIDs) {
        for (UUID entityUUID : entityUUIDs) {
            entityVisibility.putIfAbsent(entityUUID, new EntityVisibilityAndLastCheckTime(false));
        }
    }

    public void addEntity(UUID entityUUID) {
        entityVisibility.putIfAbsent(entityUUID, new EntityVisibilityAndLastCheckTime(false)); // Default to hidden if not already present
    }

    public void setEntityVisibility(UUID entityUUID, boolean visible) {
        entityVisibility.put(entityUUID, new EntityVisibilityAndLastCheckTime(visible)); // This can be used for both adding new entries and updating visibility
    }

    public boolean isEntityVisible(UUID entityUUID) {
        return entityVisibility.getOrDefault(entityUUID, new EntityVisibilityAndLastCheckTime(true)).visible;
        //Default to true as entities are visible unless explicitly hidden
    }

    public void removeEntity(UUID entityUUID) {
        entityVisibility.remove(entityUUID);
    }

    public Map<BlockLocation, Long> getSeenTileEntitiesMap() {
        return new HashMap<>(seenTileEntities);
    }

    public void addSeenTileEntity(BlockLocation tileEntityLocation) {
        seenTileEntities.put(tileEntityLocation, System.currentTimeMillis());
    }

    public void addSeenTileEntity(Location tileEntityLocation) {
        seenTileEntities.put(new BlockLocation(tileEntityLocation), System.currentTimeMillis());
    }

    public void removeSeenTileEntity(BlockLocation tileEntityLocation) {
        seenTileEntities.remove(tileEntityLocation); // This method should never actually be called, but it's here for completeness
    }

    public void removeSeenTileEntity(Location tileEntityLocation) {
        seenTileEntities.remove(new BlockLocation(tileEntityLocation)); // This method should never actually be called, but it's here for completeness
    }

    public boolean hasSeenTileEntity(BlockLocation tileEntityLocation) {
        return seenTileEntities.containsKey(tileEntityLocation);
    }

    public boolean hasSeenTileEntity(Location tileEntityLocation) {
        return seenTileEntities.containsKey(new BlockLocation(tileEntityLocation));
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    }
}
