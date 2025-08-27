package games.cubi.raycastedAntiESP.utils;

import games.cubi.raycastedAntiESP.Logger;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSafeLoc //TBH I still don't even know if this is needed
{
    private final UUID world;
    private final Vector vector1;
    private final Vector vector2;
    private volatile byte pointer = 1;
    private final AtomicBoolean writeLock = new AtomicBoolean(false);


    public ThreadSafeLoc(Vector vec, UUID world) {
        writeLock.set(true);
        this.world = world;
        vector1 = vec.clone(); //clone to ensure a new vector
        writeLock.set(false);
        vector2 = vector1.clone();
    }

    public ThreadSafeLoc(Location loc) {
        this(loc.toVector(), loc.getWorld().getUID());
    }

    public ThreadSafeLoc(Location loc, double height) {
        this(loc.add(0, height/2, 0).toVector(), loc.getWorld().getUID());
    }

    public ThreadSafeLoc(QuantisedLocation loc) {
        this(new Vector(loc.realX(), loc.realY(), loc.realZ()), loc.world());
    }

    /**This returns the actual vector, so make sure it is cloned before use*/
    private Vector getPointedVector() {
        if (pointer == 1) return vector1;
        return vector2;
    }

    public Vector getPointedVectorClone() {
        return getPointedVector().clone();
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
                overwriteVector(vector1, newVec);
                pointer = 1;
                overwriteVector(vector2, vector1);
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
                overwriteVector(vector2, vector1);
                return true;
            } finally {
                boolean sanityCheck = writeLock.compareAndSet(true, false);
                if (!sanityCheck) Logger.error(new RuntimeException("ThreadSafeLoc lost thread lock during operation"));
            }
        }
        else return false;
    }

    /**
     * Overwrites the first object with the x,y,z values of the second object to avoid creating new vector objects
     @param object the vector being overwritten
     @param newVec the source of the x,y,z values
     @return the overwritten vector (same as object param)
     * **/
    @SuppressWarnings("UnusedReturnValue")
    private Vector overwriteVector(Vector object, Vector newVec) {
        object.setX(newVec.getX()).setY(newVec.getY()).setZ(newVec.getZ());
        return object;
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

