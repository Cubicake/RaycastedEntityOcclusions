package games.cubi.raycastedAntiESP.locatables.block;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.Locatable;
import org.bukkit.Location;
import org.bukkit.World;

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
    public LocatableType getType() {
        return LocatableType.ImmutableBlockLocation;
    }

    @Override
    public Location toBukkitLocation() {
        return AbstractBlockLocation.super.toBukkitLocation();
    }

    @Override
    public Locatable add(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

    public UUID world() {
        return world;
    }

    @Override
    public int blockX() {
        return x;
    }

    @Override
    public int blockY() {
        return y;
    }

    @Override
    public int blockZ() {
        return z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
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
    public String toString() {
        return toStringForm();
    }
}
