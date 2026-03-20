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

public class ThreadSafeLocatable implements Locatable {
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
        long stamp = lock.tryOptimisticRead();
        double result = x*x + y*y + z*z;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = x*x + y*y + z*z;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
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
        double[] otherPosition = locatable.getAtomicPositionArray();
        withWriteLock(() -> {
            x += otherPosition[0];
            y += otherPosition[1];
            z += otherPosition[2];
        });
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        double[] otherPosition = locatable.getAtomicPositionArray();
        withWriteLock(() -> {
            x -= otherPosition[0];
            y -= otherPosition[1];
            z -= otherPosition[2];
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
        long stamp = lock.tryOptimisticRead();
        UUID result = world;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = world;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public int blockX() {
        return (int) Math.floor(x());
    }

    @Override
    public int blockY() {
        return (int) Math.floor(y());
    }

    @Override
    public int blockZ() {
        return (int) Math.floor(z());
    }

    @Override
    public double x() {
        long stamp = lock.tryOptimisticRead();
        double result = x;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = x;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public double y() {
        long stamp = lock.tryOptimisticRead();
        double result = y;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public double z() {
        long stamp = lock.tryOptimisticRead();
        double result = z;
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = z;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    @Override
    public double[] getAtomicPositionArray() {
        long stamp = lock.tryOptimisticRead();
        double[] result = new double[]{x, y, z};
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                result = new double[]{x, y, z};
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return result;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }
}

