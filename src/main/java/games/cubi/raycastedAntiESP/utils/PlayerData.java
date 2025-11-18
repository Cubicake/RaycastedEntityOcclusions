package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.data.DataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public Set<UUID> getEntitiesNeedingRecheck(int recheckTicks) {
        int currentTime = DataHolder.getTick();
        Set<UUID> recheckList = new HashSet<>();

        for (Map.Entry<UUID, EntityVisibilityAndLastCheckTime> values : entityVisibility.entrySet()) {
            if ((values.getValue().visible) && (currentTime - values.getValue().lastChecked < recheckTicks)) continue;
            recheckList.add(values.getKey());
        }

        return recheckList;
    }

    public void removeEntity(UUID entityUUID) {
        entityVisibility.remove(entityUUID);
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    }
}
