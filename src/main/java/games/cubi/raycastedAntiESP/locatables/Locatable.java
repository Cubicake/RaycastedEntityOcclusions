package games.cubi.raycastedAntiESP.locatables;


import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.*;

import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

// A vector-like interface representing a location in a 3D space within a specific world.
@SuppressWarnings("UnstableApiUsage")
public interface Locatable extends Position {

    default Location toBukkitLocation(){
        return new Location(Bukkit.getWorld(world()), x(), y(), z());
    }

    default double length() {
        return Math.sqrt(lengthSquared());
    }

    default double lengthSquared() {
        return x() * x() +  y() * y() + z() * z();
    }

    default double distance(Locatable locatable) {
        return Math.sqrt(distanceSquared(locatable));
    }

    default double distanceSquared(Locatable locatable) {
        double dx = x() - locatable.x();
        double dy = y() - locatable.y();
        double dz = z() - locatable.z();
        return dx * dx + dy * dy + dz * dz;
    }

    default Locatable clonePlainAndCentreIfBlockLocation() {
        return convertLocatable(this, LocatableType.Plain, true);
    }

    default Locatable normalize() {
        double length = length();
        return scalarMultiply(1.0 / length);
    }

    @SuppressWarnings("UnstableApiUsage") double x();
    @SuppressWarnings("UnstableApiUsage") double y();
    @SuppressWarnings("UnstableApiUsage") double z();

    @Override @SuppressWarnings("UnstableApiUsage")
    default int blockX() {
        return (int) Math.floor(x());
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default int blockY() {
        return (int) Math.floor(y());
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default int blockZ() {
        return (int) Math.floor(z());
    }

    Locatable add(Locatable locatable);

    Locatable subtract(Locatable locatable);

    Locatable scalarMultiply(double factor);

    UUID world();

    LocatableType getType();

    default String toStringForm() {
        return getType()+
                "{" +
                "world=" + world() +
                ", x=" + x() +
                ", y=" + y() +
                ", z=" + z() +
                '}';
    }

    enum LocatableType {
        ThreadSafe,
        Bukkit,
        MutableBlockVector,
        ImmutableBlockLocation,
        Plain,
    }

    static Locatable convertLocatable(Locatable from, LocatableType to, boolean clone) {
        switch (to) {
            case ThreadSafe -> {
                if ((from instanceof ThreadSafeLocation) && !clone) return from;
                return new ThreadSafeLocation(from.world(), from.x(), from.y(), from.z());
            }
            case Bukkit -> {
                if ((from instanceof Location) && !clone) return from;
                return new WrappedBukkitLocation(from.world(), from.x(), from.y(), from.z());
            }
            case MutableBlockVector -> {
                if ((from instanceof MutableBlockVector) && !clone) return from;
                return new MutableBlockVector(from.world(), from.x(), from.y(), from.z());
            }
            case ImmutableBlockLocation -> {
                if ((from instanceof BlockLocation) && !clone) return from;
                return new BlockLocation(from.world(), from.x(), from.y(), from.z());
            }
            case Plain -> {
                if ((from instanceof LocatableImpl) && !clone) return from;
                return new LocatableImpl(from.world(), from.x(), from.y(), from.z());
            }
            default -> {
                Logger.error(new RuntimeException("Locatable.convertLocatable: Unhandled LocatableType conversion to: " + to), 2);
                return new LocatableImpl(from.world(), from.x(), from.y(), from.z());
            }
        }
    }

    static Locatable convertLocatable(Location from, LocatableType to, boolean clone) {
        return convertLocatable((Locatable) WrappedBukkitLocation.wrap(from), to, clone);
    }

    default boolean isEqualTo(Object thatOne) {
        if (this == thatOne) return true;
        if (!(thatOne instanceof Locatable that)) return false;
        if (!(this.world().equals(that.world()))) return false;

        if (Double.doubleToLongBits(this.x()) != Double.doubleToLongBits(that.x())) {
            return false;
        }
        if (Double.doubleToLongBits(this.y()) != Double.doubleToLongBits(that.y())) {
            return false;
        }
        if (Double.doubleToLongBits(this.z()) != Double.doubleToLongBits(that.z())) {
            return false;
        }

        return true;
    }

    default int makeHash() {
        int hash = 3;

        hash = 19 * hash + this.world().hashCode();
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x()) ^ (Double.doubleToLongBits(this.x()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y()) ^ (Double.doubleToLongBits(this.y()) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.z()) ^ (Double.doubleToLongBits(this.z()) >>> 32));
        return hash;
    }

    static Locatable copyOf(Locatable locatable) {
        return convertLocatable(locatable, locatable.getType(), true);
    }

    static Locatable copyOf(Location location) {
        return convertLocatable(location, LocatableType.Bukkit, true);
    }

    static Locatable create(UUID world, double x, double y, double z, LocatableType type) {
        switch (type) {
            case ThreadSafe -> {
                return new ThreadSafeLocation(world, x, y, z);
            }
            case Bukkit -> {
                return new WrappedBukkitLocation(world, x, y, z);
            }
            case MutableBlockVector -> {
                return new MutableBlockVector(world, x, y, z);
            }
            case Plain -> {
                return new LocatableImpl(world, x, y, z);
            }
            default -> {
                Logger.error(new RuntimeException("Locatable.create: Unhandled LocatableType " + type),2);
                return new LocatableImpl(world, x, y, z);
            }
        }
    }

    static Locatable create(UUID world, double x, double y, double z) {
        return create(world, x, y, z, LocatableType.Plain);
    }
}
