package games.cubi.raycastedAntiESP.locatables;

import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.util.Vector;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

@SuppressWarnings("UnstableApiUsage")
public class ThreadSafeLocatable implements Locatable /*TBH I still don't even know if this is needed */ {
    private volatile UUID world;

    private double x, y, z;

    private final StampedLock lock = new StampedLock();


    public ThreadSafeLocatable(Vector vec, UUID world) {
        this.world = world;
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
    }

    public ThreadSafeLocatable(World world, double x, double y, double z) {
        this.world = world.getUID();
        this.x = x; this.y = y; this.z = z;
    }

    public ThreadSafeLocatable(UUID world, double x, double y, double z) {
        this.world = world;
        this.x = x; this.y = y; this.z = z;
    }

    public ThreadSafeLocatable(Location loc) {
        this(loc.toVector(), loc.getWorld().getUID());
    }

    public ThreadSafeLocatable(Location loc, double height) {
        this(loc.add(0, height/2, 0).toVector(), loc.getWorld().getUID());
    }

    /*public ThreadSafeLocation(QuantisedLocation loc) {
        this(new Vector(loc.realX(), loc.realY(), loc.realZ()), loc.world());
    }*/


    /**Returns the X value of the vector*/
    public double readX() {
        long stamp = lock.tryOptimisticRead();
        double val = this.x;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                val = this.x;
            }
            finally {
                lock.unlockRead(stamp);
            }
        }
        return val;
    }
    /**Returns the Y value of the vector*/
    public double readY() {
        long stamp = lock.tryOptimisticRead();
        double val = this.y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                val = this.y;
            }
            finally {
                lock.unlockRead(stamp);
            }
        }
        return val;
    }
    /**Returns the Z value of the vector*/
    public double readZ() {
        long stamp = lock.tryOptimisticRead();
        double val = this.z;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                val = this.z;
            }
            finally {
                lock.unlockRead(stamp);
            }
        }
        return val;
    }
    public UUID readWorld() { return world; }



    /**
     * Runs a write operation on x, y, z fields under a write lock.
     */
    private void withWriteLock(Runnable body) {
        long stamp = lock.writeLock();
        try {
            body.run();
        } finally {
            lock.unlockWrite(stamp);
        }
    }



    @Override
    public boolean equals(Object o) {
        return isEqualTo(o);
    }

    @Override
    public int hashCode() {
        return makeHash();
    }

    @Override
    public String toString() {
        return toStringForm();
    }

    @Override
    public LocatableType getType() {
        return LocatableType.ThreadSafe;
    }

    @Override
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    @Override
    public double lengthSquared() {
        double x=readX();
        double y=readY();
        double z=readZ();
        return x*x + y*y + z*z;
    }

    /**
     * @return Normalised internal vectors, may busy-wait if write access is locked
     */
    @Override
    public Locatable normalize() {
        withWriteLock(() -> {
            double len = Math.sqrt(x*x + y*y + z*z);
            x /= len; y /= len; z /= len;
        });
        return this;
    }

    @Override
    public Locatable add(Locatable locatable) {
        withWriteLock(() -> {
            x += locatable.x();
            y += locatable.y();
            z += locatable.z();
        });
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        withWriteLock(() -> {
            x -= locatable.x();
            y -= locatable.y();
            z -= locatable.z();
        });
        return this;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        withWriteLock(() -> { x *= factor; y *= factor; z *= factor; });
        return this;
    }

    @Override
    public UUID world() {
        return readWorld();
    }

    @Override
    public int blockX() {
        return (int) Math.floor(readX());
    }

    @Override
    public int blockY() {
        return (int) Math.floor(readY());
    }

    @Override
    public int blockZ() {
        return (int) Math.floor(readZ());
    }

    @Override
    public double x() {
        return readX();
    }

    @Override
    public double y() {
        return readY();
    }

    @Override
    public double z() {
        return readZ();
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public boolean isFine() {
        return true;
    }

    @Override
    public Position offset(int x, int y, int z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public FinePosition offset(double x, double y, double z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public BlockPosition toBlock() {
        return new BlockLocation(world, blockX(), blockY(), blockZ());
    }

    public void setWorld(UUID world) {
        this.world = world;
    }
}

