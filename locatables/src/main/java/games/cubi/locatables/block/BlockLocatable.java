package games.cubi.locatables.block;

import games.cubi.locatables.Locatable;
import games.cubi.locatables.LocatableImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public interface BlockLocatable extends Locatable {

    default Location toCentredLocation() {
        return new Location(Bukkit.getWorld(world()), blockX() + 0.5, blockY() + 0.5, blockZ() + 0.5);
    }

    default Locatable clonePlainAndCentreIfBlockLocation() {
        return new LocatableImpl(world(), blockX() + 0.5, blockY() + 0.5, blockZ() + 0.5);
    }

    default Location toBukkitLocation() {
        return toCentredLocation();
    }

    default boolean isEqual(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockLocatable other)) return false;
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

    @Override
    default double x() {
        return blockX();
    }

    @Override
    default double y() {
        return blockY();
    }

    @Override
    default double z() {
        return blockZ();
    }

    @Override
    int blockX();

    @Override
    int blockY();

    @Override
    int blockZ();
}
