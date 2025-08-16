package games.cubi.raycastedEntityOcclusion.Utils;

import games.cubi.raycastedEntityOcclusion.Logger;
import org.bukkit.Location;
import org.bukkit.util.Vector;

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

    private Vector getPointedVector() {
        if (pointer == 1) return vector1;
        return vector2;
    }

    public Vector read() {
        return getPointedVector().clone();
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
}

