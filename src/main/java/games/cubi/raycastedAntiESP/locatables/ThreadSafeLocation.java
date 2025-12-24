package games.cubi.raycastedAntiESP.locatables;

import com.google.common.base.Preconditions;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSafeLocation implements Locatable /*TBH I still don't even know if this is needed */ {
    private final UUID world;
    private final Vector vector1;
    private final Vector vector2;
    private volatile byte pointer = 1;
    private final AtomicBoolean writeLock = new AtomicBoolean(false);


    public ThreadSafeLocation(Vector vec, UUID world) {
        writeLock.set(true);
        this.world = world;
        vector1 = vec.clone(); //clone to ensure a new vector
        writeLock.set(false);
        vector2 = vector1.clone();
    }

    public ThreadSafeLocation(World world, double x, double y, double z) {
        writeLock.set(true);
        this.world = world.getUID();
        vector1 = new Vector(x, y, z);
        writeLock.set(false);
        vector2 = vector1.clone();
    }

    public ThreadSafeLocation(UUID world, double x, double y, double z) {
        writeLock.set(true);
        this.world = world;
        vector1 = new Vector(x, y, z);
        writeLock.set(false);
        vector2 = vector1.clone();
    }

    public ThreadSafeLocation(Location loc) {
        this(loc.toVector(), loc.getWorld().getUID());
    }

    public ThreadSafeLocation(Location loc, double height) {
        this(loc.add(0, height/2, 0).toVector(), loc.getWorld().getUID());
    }

    /*public ThreadSafeLocation(QuantisedLocation loc) {
        this(new Vector(loc.realX(), loc.realY(), loc.realZ()), loc.world());
    }*/

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
                if (!sanityCheck) Logger.errorAndReturn(new RuntimeException("ThreadSafeLocation lost thread lock during operation"), 1);
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
                if (!sanityCheck) Logger.errorAndReturn(new RuntimeException("ThreadSafeLocation lost thread lock during operation"), 1);
            }
        }
        else return false;
    }

    /**
     * Runs a write operation on vector1 in a safe double-buffer swap.
     * The lambda must mutate only vector1.
     * The method handles pointer flips and sync to vector2.
     *
     * In other words, this method can be used so that writing code to ThreadSafeLocations is as easy as writing to a normal Vector.
     */
    private void withWriteLock(Runnable body) {
        while (!writeLock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
        try {
            Preconditions.checkArgument(pointer == 1);
            // Switch reads to vector2
            pointer = 2;

            // Perform mutation on vector1
            body.run();

            // Switch reads back to vector1
            pointer = 1;

            // Sync vector2 with updated vector1
            overwriteVector(vector2, vector1);

        } finally {
            if (!writeLock.compareAndSet(true, false)) {
                Logger.errorAndReturn(new RuntimeException("ThreadSafeLocation lost thread lock during operation"), 1);
            }
        }
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
    public Location toBukkitLocation() {
        throw new RuntimeException("Unimplemented");
        //TODO: Implement
    }

    @Override
    public LocatableType getType() {
        return LocatableType.ThreadSafe;
    }

    @Override
    public double length() {
        return getPointedVector().length();
    }

    @Override
    public double lengthSquared() {
        return getPointedVector().lengthSquared();
    }

    /**
     * @return Normalised internal vectors, may busy-wait if write access is locked
     */
    @Override
    public Locatable normalize() {
        withWriteLock(vector1::normalize);
        return this;
    }

    @Override
    public Locatable add(Locatable locatable) {
        withWriteLock(() -> {
            vector1.setX(vector1.getX() + locatable.x());
            vector1.setY(vector1.getY() +  locatable.y());
            vector1.setZ(vector1.getZ() + locatable.z());
        });
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        withWriteLock(() -> {
            vector1.setX(vector1.getX() - locatable.x());
            vector1.setY(vector1.getY() -  locatable.y());
            vector1.setZ(vector1.getZ() - locatable.z());
        });
        return this;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        withWriteLock(() -> vector1.multiply(factor));
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
}

