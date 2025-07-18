package games.cubi.raycastedEntityOcclusion.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class BlockLocation {
    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    public BlockLocation(World world, double x, double y, double z) {
        this.world = world.getUID();
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.z = (int) Math.floor(z);
    }

    public BlockLocation(Location loc) {
        this.world = loc.getWorld().getUID();
        this.x = (int) Math.floor(loc.getX());
        this.y = (int) Math.floor(loc.getY());
        this.z = (int) Math.floor(loc.getZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location toCentredLocation() {
        return new Location(Bukkit.getWorld(world), x + 0.5, y + 0.5, z + 0.5);
    }

}
