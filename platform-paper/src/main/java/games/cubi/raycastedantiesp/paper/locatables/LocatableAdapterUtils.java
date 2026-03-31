package games.cubi.raycastedantiesp.paper.locatables;

import games.cubi.locatables.Locatable;
import games.cubi.locatables.BlockLocatable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocatableAdapterUtils {

    public static Location toCentredLocation(BlockLocatable locatable) {
        return new Location(Bukkit.getWorld(locatable.world()), locatable.blockX() + 0.5, locatable.blockY() + 0.5, locatable.blockZ() + 0.5);
    }

    public static Location toBukkitLocation(Locatable locatable) {
        return new Location(Bukkit.getWorld(locatable.world()), locatable.x(), locatable.y(), locatable.z());
    }

    public static Locatable toLocatable(Location location, Locatable.LocatableType type) {

        UUID worldUUID = location.getWorld().getUID();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return Locatable.create(worldUUID, x, y, z, type);
    }

    public static <T extends Locatable> T toLocatable(Location location, Class<T> type) {
        return toLocatable(location, 0, type);
    }

    public static <T extends Locatable> T toLocatable(Location location, double heightOffset, Class<T> type) {

        UUID worldUUID = location.getWorld().getUID();
        double x = location.getX();
        double y = location.getY() + heightOffset;
        double z = location.getZ();

        return Locatable.create(worldUUID, x, y, z, type);
    }

    private static final Map<UUID, WeakReference<World>> worldCache = new ConcurrentHashMap<>();

    //Bukkit#getWorld iterates through all worlds and compares UUIDs, this is probably not actually needed though
    public static World getWorld(UUID worldUUID) {
        World world = null;
        WeakReference<World> weakRef = worldCache.get(worldUUID);
        if (weakRef != null) {
            world = weakRef.get();
        }
        if (world == null) {
            world = Bukkit.getWorld(worldUUID);
            if (world != null) {
                worldCache.put(worldUUID, new WeakReference<>(world));
            }
        }
        return world;
    }
}
