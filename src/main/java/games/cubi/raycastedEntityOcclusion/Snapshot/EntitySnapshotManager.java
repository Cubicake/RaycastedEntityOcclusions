package games.cubi.raycastedEntityOcclusion.Snapshot;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
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

    // put anywhere inside EntitySnapshotManager
    public @Nullable Location getLocation(Entity entity) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(entity.getWorld().getName());
        if (entitiesInWorld == null) return null;
        EntityData data = entitiesInWorld.get(entity.getUniqueId());
        return (data == null) ? null : data.getLocation();
    }


    /* ---- visibility helpers ---- */
    public void setVisibility(Entity entity, Player player, boolean canSee) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(entity.getWorld().getName());
        if (entitiesInWorld != null) {
            EntityData d = entitiesInWorld.get(entity.getUniqueId());
            if (d != null) d.setVisibleFor(player.getUniqueId(), canSee);
        }
    }

    public void clearVisibility(Entity entity, Player player) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(entity.getWorld().getName());
        if (entitiesInWorld != null) {
            EntityData d = entitiesInWorld.get(entity.getUniqueId());
            if (d != null) d.clearVisibility(player.getUniqueId());
        }
    }


    public Boolean getVisibility(Entity entity, Player player) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(entity.getWorld().getName());
        if (entitiesInWorld == null) return null;
        EntityData d = entitiesInWorld.get(entity.getUniqueId());
        return (d == null) ? null : d.getVisibility(player.getUniqueId());
    }

    /* ---- miscellaneous ---- */
    public Collection<EntityData> getEntitiesInWorld(World world) {
        Map<UUID, EntityData> entitiesInWorld = cache.get(world.getName());
        return (entitiesInWorld == null) ? Collections.emptyList() : entitiesInWorld.values();
    }

    public List<Entity> getBukkitEntitiesNear(Location origin, int radiusX, int radiusY, int radiusZ) {

        World world = origin.getWorld();
        Map<UUID, EntityData> entitiesInWorld = cache.get(world.getName());
        if (entitiesInWorld == null || entitiesInWorld.isEmpty()) {
            return Collections.emptyList();
        }

        double ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        double minX = ox - radiusX, maxX = ox + radiusX;
        double minY = oy - radiusY, maxY = oy + radiusY;
        double minZ = oz - radiusZ, maxZ = oz + radiusZ;

        List<Entity> result = new ArrayList<>();
        for (Map.Entry<UUID, EntityData> entry : entitiesInWorld.entrySet()) {
            EntityData data = entry.getValue();
            Location loc = data.getLocation();
            double x = loc.getX(), y = loc.getY(), z = loc.getZ();

            if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ) {

                Entity live = world.getEntity(entry.getKey());
                if (live != null) {
                    if (!live.isDead()) {
                        result.add(live);
                    }
                    else removeEntity(live);
                }
            }
        }
        return result;
    }

    public int size() {
        return cache.values().stream().mapToInt(Map::size).sum();
    }
}
