package games.cubi.raycastedAntiESP.locatables.block;

import io.papermc.paper.math.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public interface AbstractBlockLocation extends BlockPosition {
    UUID world();

    default Location toCentredLocation() {
        return new Location(Bukkit.getWorld(world()), blockX() + 0.5, blockY() + 0.5, blockZ() + 0.5);
    }

    default Location toBukkitLocation() {
        return toCentredLocation();
    }

    default boolean isEqual(AbstractBlockLocation thisOne, Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractBlockLocation other)) return false;
        return thisOne.x() == other.x() && thisOne.y() == other.y() && thisOne.z() == other.z() && world().equals(other.world());
    }

    default int hash(AbstractBlockLocation thisOne) {
        int result = 17;
        result = 31 * result + (thisOne.world() != null ? thisOne.world().hashCode() : 0);
        result = 31 * result + thisOne.blockX();
        result = 31 * result + thisOne.blockY();
        result = 31 * result + thisOne.blockZ();
        return result;
    }
}
