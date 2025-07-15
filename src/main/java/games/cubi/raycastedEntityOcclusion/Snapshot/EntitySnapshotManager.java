package games.cubi.raycastedEntityOcclusion.Snapshot;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class EntitySnapshotManager {

    public static class EntityData {

        //Entity data for a single entity in a world.

        private volatile Location location;
        /** Player UUID → can-see flag (true/false).  Absence = NOT_SET. */
        private final Map<UUID, Boolean> visibility = new ConcurrentHashMap<>();

        public EntityData(Location initial) {
            this.location = initial.clone();
        }

        /* ---- location ---- */
        public Location getLocation() { return location.clone(); }
        public void setLocation(Location loc) { this.location = loc.clone(); }

        /* ---- visibility ---- */
        public void setVisibleFor(UUID playerId, boolean canSee) {
            visibility.put(playerId, canSee);
        }
        public void clearVisibility(UUID playerId) { visibility.remove(playerId); }

        public Boolean getVisibility(UUID playerId) {
            return visibility.get(playerId);         // may be null
        }
    }

    // Cache: world-name → (entity-UUID → data)
    private final Map<String, Map<UUID, EntityData>> cache = new ConcurrentHashMap<>();

    public void addEntity(Entity entity) {
        Objects.requireNonNull(entity, "EntitySnapshotManager: Cannot add null entity");
        cache.computeIfAbsent(entity.getWorld().getName(),
                        w -> new ConcurrentHashMap<>())
                .putIfAbsent(entity.getUniqueId(),
                        new EntityData(entity.getLocation()));
    }

    public void removeEntity(Entity entity) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(entity.getWorld().getName());
        if (entitiesInWorld != null) {
            entitiesInWorld.remove(entity.getUniqueId());
            if (entitiesInWorld.isEmpty()) cache.remove(entity.getWorld().getName());
        }
    }

    /** Updates (or inserts) the latest location for the entity. */
    public void updateLocation(Entity entity) {
        cache.computeIfAbsent(entity.getWorld().getName(),
                        w -> new ConcurrentHashMap<>())
                .compute(entity.getUniqueId(), (id, data) -> {
                    if (data == null) return new EntityData(entity.getLocation());
                    data.setLocation(entity.getLocation());
                    return data;
                });
    }

    /* ---- visibility helpers ---- */
    public void setVisibility(Entity entity, Player player, boolean canSee) {
        Map<UUID, EntityData> byWorld = cache.get(entity.getWorld().getName());
        if (byWorld != null) {
            EntityData d = byWorld.get(entity.getUniqueId());
            if (d != null) d.setVisibleFor(player.getUniqueId(), canSee);
        }
    }

    public void clearVisibility(Entity entity, Player player) {
        Map<UUID, EntityData> byWorld = cache.get(entity.getWorld().getName());
        if (byWorld != null) {
            EntityData d = byWorld.get(entity.getUniqueId());
            if (d != null) d.clearVisibility(player.getUniqueId());
        }
    }


    public Boolean getVisibility(Entity entity, Player player) {
        Map<UUID, EntityData> byWorld = cache.get(entity.getWorld().getName());
        if (byWorld == null) return null;
        EntityData d = byWorld.get(entity.getUniqueId());
        return (d == null) ? null : d.getVisibility(player.getUniqueId());
    }

    /* ---- miscellaneous ---- */
    public Collection<EntityData> getEntitiesInWorld(World world) {
        Map<UUID, EntityData> byWorld = cache.get(world.getName());
        return (byWorld == null) ? Collections.emptyList() : byWorld.values();
    }

    public int size() {
        return cache.values().stream().mapToInt(Map::size).sum();
    }
}
