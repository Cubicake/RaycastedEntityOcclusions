package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.locatables.Locatable;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class BlockLocation implements BlockPosition {
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

    public Location toCentredLocation() {
        return new Location(Bukkit.getWorld(world), x + 0.5, y + 0.5, z + 0.5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockLocation other)) return false;
        return x == other.x && y == other.y && z == other.z && world.equals(other.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    public UUID world() {
        return world;
    }

/**
 * @return Centred Bukkit Location
 */
    public Location toBukkitLocation() {
        return toCentredLocation();
    }

    public Locatable.LocatableType getType() {
        return Locatable.LocatableType.Block;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockX() {
        return x;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockY() {
        return y;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockZ() {
        return z;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double x() {
        return x;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double y() {
        return y;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double z() {
        return z;
    }
}
