package games.cubi.locatables;

import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

public class ThreadSafeLocatable implements MutableLocatable {
    private volatile UUID world;

    private double x, y, z;

    private final StampedLock lock = new StampedLock();

    public ThreadSafeLocatable(UUID world, double x, double y, double z) {
        this.world = world;
        this.x = x; this.y = y; this.z = z;
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
    public MutableLocatable normalize() {
        withWriteLock(() -> {
            double len = Math.sqrt(x*x + y*y + z*z);
            x /= len; y /= len; z /= len;
        });
        return this;
    }

    @Override
    public MutableLocatable add(Locatable locatable) {
        double[] otherPosition = locatable.getAtomicPositionArray();
        withWriteLock(() -> {
            x += otherPosition[0];
            y += otherPosition[1];
            z += otherPosition[2];
        });
        return this;
    }

    @Override
    public MutableLocatable subtract(Locatable locatable) {
        double[] otherPosition = locatable.getAtomicPositionArray();
        withWriteLock(() -> {
            x -= otherPosition[0];
            y -= otherPosition[1];
            z -= otherPosition[2];
        });
        return this;
    }

    @Override
    public MutableLocatable scalarMultiply(double factor) {
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

    @Override
    public MutableLocatable setX(double x) {
        withWriteLock(() -> this.x = x);
        return this;
    }

    @Override
    public MutableLocatable setY(double y) {
        withWriteLock(() -> this.y = y);
        return this;
    }

    @Override
    public MutableLocatable setZ(double z) {
        withWriteLock(() -> this.z = z);
        return this;
    }

    @Override
    public MutableLocatable setWorld(UUID world) {
        this.world = world;
        return this;
    }

    @Override
    public MutableLocatable set(Locatable locatable) {
        double[] otherPosition = locatable.getAtomicPositionArray();
        withWriteLock(() -> {
            this.world = locatable.world(); // technically this could be racy, but the world shouldn't be changing often
            this.x = otherPosition[0];
            this.y = otherPosition[1];
            this.z = otherPosition[2];
        });
        return this;
    }

    @Override
    public MutableLocatable set(double x, double y, double z, UUID world) {
        withWriteLock(() -> {
            this.world = world;
            this.x = x; this.y = y; this.z = z;
        });
        return this;
    }
}

