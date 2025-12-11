package games.cubi.raycastedAntiESP.locatables.block;

import games.cubi.raycastedAntiESP.locatables.Locatable;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class MutableBlockVector implements AbstractBlockLocation, Locatable {
    private final UUID world;
    private double mutableX;
    private double mutableY;
    private double mutableZ;

    public MutableBlockVector(UUID world, double x, double y, double z) {
        this.world = world;
        this.mutableX = x;
        this.mutableY = y;
        this.mutableZ = z;
    }

    public MutableBlockVector(Location loc) {
        this.world = loc.getWorld().getUID();
        this.mutableX = loc.getX();
        this.mutableY = loc.getY();
        this.mutableZ = loc.getZ();
    }

    public MutableBlockVector(UUID world, int x, int y, int z) {
        this.world = world;
        this.mutableX = x;
        this.mutableY = y;
        this.mutableZ = z;
    }

    @Override
    public LocatableType getType() {
        return LocatableType.MutableBlockVector;
    }

    @Override
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    @Override
    public double lengthSquared() {
        return mutableX*mutableX + mutableY*mutableY + mutableZ*mutableZ;
    }

    @Override
    public Locatable normalize() {
        double length = length();
        this.mutableX /= length;
        this.mutableY /= length;
        this.mutableZ /= length;
        return this;
    }

    @Override
    public Locatable add(Locatable locatable) {
        this.mutableX += locatable.x();
        this.mutableY +=  locatable.y();
        this.mutableZ += locatable.z();
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        this.mutableX -= locatable.x();
        this.mutableY -=  locatable.y();
        this.mutableZ -= locatable.z();
        return this;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        this.mutableX *= factor;
        this.mutableY *= factor;
        this.mutableZ *= factor;
        return this;
    }

    @Override
    public BlockPosition offset(int x, int y, int z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public FinePosition offset(double x, double y, double z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public UUID world() {
        return world;
    }

    @Override
    public Location toBukkitLocation() {
        return AbstractBlockLocation.super.toBukkitLocation();
    }

    /**
 * This checks equality with AbstractBlockLocations, not Locatables. Use Locatable#isEqualTo for that. Thus, hashmap lookups are compatible with only AbstractBlockLocations, not Locatables.
 */
    @Override
    public boolean equals(Object o) {
        return isEqual(o);
    }

    @Override
    public int hashCode() {
        return hash();
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockX() {
        return (int) Math.floor(mutableX);
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockY() {
        return (int) Math.floor(mutableY);
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public int blockZ() {
        return (int) Math.floor(mutableZ);
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
