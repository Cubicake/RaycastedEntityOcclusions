package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.data.DataHolder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    private record EntityVisibilityAndLastCheckTime(boolean visible, int lastChecked) { //todo: This will result in a lot of object churn, consider using mutable objects if profiling shows this to be an issue
        public EntityVisibilityAndLastCheckTime(boolean visible) {
            this(visible, DataHolder.getTick());
        }
    }

    // Maps must be thread-safe as their values will be updated while async engine jobs are running, but UUID is probably fine.
    private final UUID playerUUID;
    private boolean hasBypassPermission;

    // UUID = Entity UUID, Boolean = if it is visible to the player. False = hidden
    private final ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> entityVisibility = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> playerVisibility = new ConcurrentHashMap<>();

    public PlayerData(UUID playerUUID, boolean hasBypassPermission) {
        this.playerUUID = playerUUID;
        this.hasBypassPermission = hasBypassPermission;
    }

    public PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.hasBypassPermission = player.hasPermission("raycastedantiesp.bypass");
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    private void addMultipleGeneric(Set<UUID> entityUUIDs, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        for (UUID entityUUID : entityUUIDs) {
            generic.putIfAbsent(entityUUID, new EntityVisibilityAndLastCheckTime(false));
        }
    }

    private void addGeneric(UUID entityUUID, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        generic.putIfAbsent(entityUUID, new EntityVisibilityAndLastCheckTime(false)); // Default to hidden if not already present
    }

    private void setVisibilityGeneric(UUID entityUUID, boolean visible, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        generic.put(entityUUID, new EntityVisibilityAndLastCheckTime(visible)); // This can be used for both adding new entries and updating visibility
    }

    private boolean isVisibleGeneric(UUID entityUUID, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        return generic.computeIfAbsent(entityUUID,
                k -> new EntityVisibilityAndLastCheckTime(true))
                .visible;        //Default to true as entities are visible unless explicitly hidden
    }

    private Set<UUID> getNeedingRecheckGeneric(int recheckTicks, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        int currentTime = DataHolder.getTick();
        Set<UUID> recheckList = new HashSet<>();

        for (Map.Entry<UUID, EntityVisibilityAndLastCheckTime> values : generic.entrySet()) {
            if ((values.getValue().visible) && (currentTime - values.getValue().lastChecked < recheckTicks)) continue;
            recheckList.add(values.getKey());
        }

        return recheckList;
    }

    private void removeEntityGeneric(UUID entityUUID, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        generic.remove(entityUUID);
    }

    /**
     * Adds the provided set of entity UUIDs to the entity visibility map with a default visibility of false (not visible). If an entity UUID already exists in the map, it will not be modified.
     * **/
    public void addEntities(Set<UUID> entityUUIDs) {
        addMultipleGeneric(entityUUIDs, entityVisibility);
    }

    public void addEntity(UUID entityUUID) {
        addGeneric(entityUUID, entityVisibility);
    }

    public void setEntityVisibility(UUID entityUUID, boolean visible) {
        setVisibilityGeneric(entityUUID, visible, entityVisibility);
    }

    /**
     * Atomically sets the visibility of an entity if it differs from the current value.
     * Updates the timestamp even if the visibility remains the same.
     * @param entityUUID The UUID of the entity
     * @param newVisibility The new visibility state
     * @return true if the visibility was changed, false if it was already set to newVisibility
     */
    private boolean compareAndSetGenericVisibility(UUID entityUUID, boolean newVisibility, ConcurrentHashMap<UUID, EntityVisibilityAndLastCheckTime> generic) {
        final boolean[] changed = {false};
        generic.compute(entityUUID, (key, current) -> {

            if ((current == null) || (current.visible != newVisibility)) {
                changed[0] = true;
            }

            return new EntityVisibilityAndLastCheckTime(newVisibility);
        });

        return changed[0];
    }
    /**
     * Atomically sets the visibility of an entity if it differs from the current value.
     * Updates the timestamp even if the visibility remains the same.
     * @param entityUUID The UUID of the entity
     * @param newVisibility The new visibility state
     * @return true if the visibility was changed, false if it was already set to newVisibility
     */
    public boolean compareAndSetEntityVisibility(UUID entityUUID, boolean newVisibility) {
        return compareAndSetGenericVisibility(entityUUID, newVisibility, entityVisibility);
    }
    /**
     * Atomically sets the visibility of an entity if it differs from the current value.
     * Updates the timestamp even if the visibility remains the same.
     * @param otherPlayerUUID The UUID of the entity
     * @param newVisibility The new visibility state
     * @return true if the visibility was changed, false if it was already set to newVisibility
     */
    public boolean compareAndSetPlayerVisibility(UUID otherPlayerUUID, boolean newVisibility) {
        return compareAndSetGenericVisibility(otherPlayerUUID, newVisibility, playerVisibility);
    }

    public boolean isEntityVisible(UUID entityUUID) {
        return isVisibleGeneric(entityUUID, entityVisibility);
        //Default to true as entities are visible unless explicitly hidden
    }

    public Set<UUID> getEntitiesNeedingRecheck(int recheckTicks) {
        return getNeedingRecheckGeneric(recheckTicks, entityVisibility);
    }

    public void removeEntity(UUID entityUUID) {
        removeEntityGeneric(entityUUID, entityVisibility);
    }

    /**
     * Adds the provided set of player UUIDs to the entity visibility map with a default visibility of false (not visible). If an entity UUID already exists in the map, it will not be modified.
     * **/
    public void addPlayers(Set<UUID> entityUUIDs) {
        addMultipleGeneric(entityUUIDs, playerVisibility);
    }

    public void addPlayer(UUID entityUUID) {
        addGeneric(entityUUID, playerVisibility);
    }

    public void setPlayerVisibility(UUID entityUUID, boolean visible) {
        setVisibilityGeneric(entityUUID, visible, playerVisibility);
    }

    public boolean isPlayerVisible(UUID entityUUID) {
        return isVisibleGeneric(entityUUID, playerVisibility);
        //Default to true as entities are visible unless explicitly hidden
    }

    public Set<UUID> getPlayersNeedingRecheck(int recheckTicks) {
        return getNeedingRecheckGeneric(recheckTicks, playerVisibility);
    }

    public void removePlayer(UUID entityUUID) {
        removeEntityGeneric(entityUUID, playerVisibility);
    }

    public boolean hasBypassPermission() {
        return hasBypassPermission;
    }

    public void setBypassPermission(boolean hasBypassPermission) {
        this.hasBypassPermission = hasBypassPermission;
    }
}
