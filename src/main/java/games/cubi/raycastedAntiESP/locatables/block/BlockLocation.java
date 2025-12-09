package games.cubi.raycastedAntiESP.locatables.block;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class BlockLocation implements AbstractBlockLocation {
    protected final UUID world;
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

    @Override
    public boolean equals(Object o) {
        return isEqual(this, o);
    }

    @Override
    public int hashCode() {
        return hash(this);
    }

    public UUID world() {
        return world;
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
