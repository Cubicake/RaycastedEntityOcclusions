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

    default boolean isEqual(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractBlockLocation other)) return false;
        return this.blockX() == other.blockX() && this.blockY() == other.blockY() && this.blockZ() == other.blockZ() && world().equals(other.world());
    }

    default int hash() {
        int result = 17;
        result = 31 * result + (this.world() != null ? this.world().hashCode() : 0);
        result = 31 * result + this.blockX();
        result = 31 * result + this.blockY();
        result = 31 * result + this.blockZ();
        return result;
    }
}
