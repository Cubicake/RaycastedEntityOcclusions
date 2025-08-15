package games.cubi.raycastedEntityOcclusion.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerData {
    // Maps must be thread-safe as their values will be updated while async engine jobs are running, but UUID is probably fine.
    private final UUID playerUUID;
    private final boolean hasBypassPermission;

    // UUID = Entity UUID, Boolean = if it is visible to the player. False = hidden
    private final ConcurrentHashMap<UUID, Boolean> entityVisibility = new ConcurrentHashMap<>();

    // Location, time in millis TODO: Is it necessary to store the time as a long? Do we even need a recheck interval at all?
    private final ConcurrentHashMap<BlockLocation, Long> seenTileEntities = new ConcurrentHashMap<>();

    private final AtomicInteger ticksSinceVisibleEntityRecheck = new AtomicInteger(0);

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

    public Map<UUID, Boolean> getEntityVisibilityMap() {
        return new HashMap<>(entityVisibility);
    }

    public void addEntities(Set<UUID> entityUUIDs) {
        for (UUID entityUUID : entityUUIDs) {
            entityVisibility.putIfAbsent(entityUUID, true);
        }
    }

    public void addEntity(UUID entityUUID) {
        entityVisibility.putIfAbsent(entityUUID, true); // Default to visible if not already present
    }

    public void setEntityVisibility(UUID entityUUID, boolean visible) {
        entityVisibility.put(entityUUID, visible); // This can be used for both adding new entries and updating visibility
    }

    public boolean isEntityVisible(UUID entityUUID) {
        return entityVisibility.getOrDefault(entityUUID, true);
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

    public void incrementTicksSinceVisibleEntityRecheck() {
        ticksSinceVisibleEntityRecheck.incrementAndGet();
    }

    public void resetTicksSinceVisibleEntityRecheck() {
        ticksSinceVisibleEntityRecheck.set(0);
    }

    public int getTicksSinceVisibleEntityRecheck() {
        return ticksSinceVisibleEntityRecheck.get();
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }
}
