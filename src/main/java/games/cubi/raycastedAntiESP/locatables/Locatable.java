package games.cubi.raycastedAntiESP.locatables;


import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// A vector-like interface representing a location in a 3D space within a specific world.
public interface Locatable extends Position {

    Location toBukkitLocation();

    LocatableType getType();

    double length();

    double lengthSquared();

    default double distance(Locatable locatable) {
        return Math.sqrt(distanceSquared(locatable));
    }

    default double distanceSquared(Locatable locatable) {
        double dx = x() - locatable.x();
        double dy = y() - locatable.y();
        double dz = z() - locatable.z();
        return dx * dx + dy * dy + dz * dz;
    }

    Locatable normalize();

    Locatable add(Locatable locatable);

    Locatable subtract(Locatable locatable);

    Locatable scalarMultiply(double factor);

    UUID world();

    enum LocatableType {
        ThreadSafe,
        Bukkit,
        MutableBlockVector,
    }

    static Locatable convertLocatable(Locatable from, LocatableType to, boolean clone) {
        Locatable returnObject = null;
        switch (to) {
            case ThreadSafe -> {
                if ((from instanceof ThreadSafeLocation) && !clone) return from;
                return new ThreadSafeLocation(from.world(), from.x(), from.y(), from.z());
            }
            case Bukkit -> {
                if ((from instanceof Location) && !clone) return from;
                return new WrappedBukkitLocation(from.world(), from.x(), from.y(), from.z());
            }
        }
        return returnObject;
    }

    static Locatable convertLocatable(Location from, LocatableType to, boolean clone) {
        return convertLocatable((Locatable) WrappedBukkitLocation.wrap(from), to, clone);
    }

    default boolean isEqualTo(Locatable thisOne, Object thatOne) {
        if (thisOne == thatOne) return true;
        if (!(thatOne instanceof Locatable that)) return false;
        if (!(thisOne.world().equals(that.world()))) return false;

        if (Double.doubleToLongBits(thisOne.x()) != Double.doubleToLongBits(that.x())) {
            return false;
        }
        if (Double.doubleToLongBits(thisOne.y()) != Double.doubleToLongBits(that.y())) {
            return false;
        }
        if (Double.doubleToLongBits(thisOne.z()) != Double.doubleToLongBits(that.z())) {
            return false;
        }

        return true;
    }

    default int makeHash(Locatable thisOne) {
        int hash = 3;

        hash = 19 * hash + thisOne.world().hashCode();
        hash = 19 * hash + (int) (Double.doubleToLongBits(thisOne.x()) ^ (Double.doubleToLongBits(thisOne.x()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(thisOne.y()) ^ (Double.doubleToLongBits(thisOne.y()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(thisOne.z()) ^ (Double.doubleToLongBits(thisOne.z()) >>> 32));
        return hash;
    }
}
