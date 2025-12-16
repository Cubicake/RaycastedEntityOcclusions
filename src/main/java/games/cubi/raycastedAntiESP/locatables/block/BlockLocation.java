package games.cubi.raycastedAntiESP.locatables.block;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.Locatable;
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

    public BlockLocation(UUID world, double x, double y, double z) {
        this.world = world;
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
        return isEqual(o);
    }

    @Override
    public int hashCode() {
        return hash();
    }

    @Override
    public LocatableType getType() {
        return LocatableType.ImmutableBlockLocation;
    }

    @Override
    public Locatable add(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"));
        return null;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"));
        return null;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"));
        return null;
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
