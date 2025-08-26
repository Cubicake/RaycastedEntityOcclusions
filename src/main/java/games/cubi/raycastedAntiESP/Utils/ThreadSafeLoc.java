package games.cubi.raycastedAntiESP.Utils;

import games.cubi.raycastedAntiESP.Logger;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSafeLoc
{
    private final UUID world;
    private volatile Vector vector1;
    private volatile Vector vector2;
    private volatile byte pointer = 1;
    private final AtomicBoolean writeLock = new AtomicBoolean(false);

    public ThreadSafeLoc(Location loc) {
        writeLock.set(true);
        world = loc.getWorld().getUID();
        vector1 = loc.toVector(); //this provides a new vector
        writeLock.set(false);
        vector2 = vector1.clone();
    }
    public ThreadSafeLoc(Location loc, double height) {
        writeLock.set(true);
        world = loc.getWorld().getUID();
        vector1 = loc.toVector(); //this provides a new vector
        vector1.setY(vector1.getY()+(height/2));
        writeLock.set(false);
        vector2 = vector1.clone();
    }
    public ThreadSafeLoc(QuantisedLocation loc) {
        writeLock.set(true);
        world = loc.world();
        vector1 = new Vector(loc.realX(), loc.realY(), loc.realZ());
        writeLock.set(false);
        vector2 = vector1.clone();
    }
    /**This returns the actual vector, so make sure it is cloned before use*/
    private Vector getPointedVector() {
        if (pointer == 1) return vector1;
        return vector2;
    }
    /**Returns a clone of the vector*/
    public Vector read() {
        return getPointedVector().clone();
    }
    /**Returns the X value of the vector*/
    public double readX() {
        return getPointedVector().getX();
    }
    /**Returns the Y value of the vector*/
    public double readY() {
        return getPointedVector().getY();
    }
    /**Returns the Z value of the vector*/
    public double readZ() {
        return getPointedVector().getZ();
    }
    public UUID readWorld() { return world; }

    public boolean update(Vector newVec) {
        if (writeLock.compareAndSet(false, true)) {
            try {
                pointer = 2;
                vector1 = newVec.clone();
                pointer = 1;
                vector2 = vector1.clone();
                return true;
            } finally {
                boolean sanityCheck = writeLock.compareAndSet(true, false);
                if (!sanityCheck) Logger.error(new RuntimeException("ThreadSafeLoc lost thread lock during operation"));
            }
        }
        else return false;
    }

    public boolean updateDelta(Vector delta) {
        if (writeLock.compareAndSet(false, true)) {
            try {
                pointer = 2;
                vector1.add(delta);
                pointer = 1;
                vector2 = vector1.clone();
                return true;
            } finally {
                boolean sanityCheck = writeLock.compareAndSet(true, false);
                if (!sanityCheck) Logger.error(new RuntimeException("ThreadSafeLoc lost thread lock during operation"));
            }
        }
        else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThreadSafeLoc other)) return false;
        return world.equals(other.world) && this.getPointedVector().equals(other.getPointedVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, getPointedVector());
    }
}

