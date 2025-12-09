package games.cubi.raycastedAntiESP.locatables.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class MutableBlockLocation implements AbstractBlockLocation {
    private final UUID world;
    private int mutableX;
    private int mutableY;
    private int mutableZ;

    public MutableBlockLocation(World world, double x, double y, double z) {
        this.world = world.getUID();
        this.mutableX = (int) Math.floor(x);
        this.mutableY = (int) Math.floor(y);
        this.mutableZ = (int) Math.floor(z);
    }

    public MutableBlockLocation(Location loc) {
        this.world = loc.getWorld().getUID();
        this.mutableX = (int) Math.floor(loc.getX());
        this.mutableY = (int) Math.floor(loc.getY());
        this.mutableZ = (int) Math.floor(loc.getZ());
    }

    public MutableBlockLocation(UUID world, int x, int y, int z) {
        this.world = world;
        this.mutableX = x;
        this.mutableY = y;
        this.mutableZ = z;
    }

    @Override
    public UUID world() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        return isEqual(this, o);
    }

    @Override
    public int hashCode() {
        return hash(this);
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockX() {
        return mutableX;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockY() {
        return mutableY;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockZ() {
        return mutableZ;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double x() {
        return mutableX;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double y() {
        return mutableY;
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public double z() {
        return mutableZ;
    }

    public void add(int dx, int dy, int dz) {
        this.mutableX += dx;
        this.mutableY += dy;
        this.mutableZ += dz;
    }

    public void set(int x, int y, int z) {
        this.mutableX = x;
        this.mutableY = y;
        this.mutableZ = z;
    }
}
