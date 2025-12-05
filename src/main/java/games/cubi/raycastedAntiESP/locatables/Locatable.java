package games.cubi.raycastedAntiESP.locatables;


import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// This extends position but is not directly the position interface as it is marked unstable, so this way if the interface changes, only Locatable needs to be updated
public interface Locatable extends Position {

    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull FinePosition offset(double x, double y, double z) {
        throw new RuntimeException("Offset not implemented");
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull FinePosition offset(int x, int y, int z) {
        throw new RuntimeException("Offset not implemented");
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull BlockPosition toBlock() {
        throw new RuntimeException("not implemented");
    }

    Location toBukkitLocation();

    LocatableType getType();

    UUID world();

    enum LocatableType {
        Quantised,
        ThreadSafe,
        Block,
        Bukkit,
    }

    static Locatable convertLocatable(Locatable from, LocatableType to) {
        Locatable returnObject = null;
        switch (to) {
            case Quantised -> {
                //returnObject = new QuantisedLocation() TODO Complete
            }
            case ThreadSafe -> {
            }
            case Block -> {
            }
            case Bukkit -> {
            }
        }
        return returnObject;
    }

    static Locatable convertLocatable(Location from, LocatableType to) {
        return convertLocatable((Locatable) WrappedBukkitLocation.wrap(from), to);
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
