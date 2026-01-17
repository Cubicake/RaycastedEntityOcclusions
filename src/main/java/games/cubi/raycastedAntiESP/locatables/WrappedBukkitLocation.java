package games.cubi.raycastedAntiESP.locatables;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WrappedBukkitLocation extends Location implements Locatable {
    public WrappedBukkitLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public WrappedBukkitLocation(UUID world, double x, double y, double z) {
        super(Bukkit.getWorld(world), x, y, z);
    }

    public static WrappedBukkitLocation wrap(Location location) {
        if (location instanceof WrappedBukkitLocation location1) {
            return location1;
        }
        return new WrappedBukkitLocation(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    @Override
    public Location toBukkitLocation() {
        return this;
    }

    @Override
    public LocatableType getType() {
        return LocatableType.Bukkit;
    }

    @Override
    public Locatable add(Locatable locatable) {
        add(locatable.x(), locatable.y(), locatable.z());
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        subtract(locatable.x(), locatable.y(), locatable.z());
        return this;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        set(x() * factor, y() * factor, z() * factor);
        return this;
    }

    @Override
    public UUID world() {
        return super.getWorld().getUID();
    }

    @Override
    public int blockX() {
        return super.blockX();
    }

    @Override
    public int blockY() {
        return super.blockY();
    }

    @Override
    public int blockZ() {
        return super.blockZ();
    }

    @Override
    public @NotNull BlockPosition toBlock() {
        return super.toBlock();
    }

    @Override
    public @NotNull FinePosition offset(int x, int y, int z) {
        return super.offset(x,y,z);
    }

    @Override
    public @NotNull FinePosition offset(double x, double y, double z) {
        return super.offset(x,y,z);
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
    public WrappedBukkitLocation clone() {
        return new WrappedBukkitLocation(this.getWorld(), this.getX(), this.getY(), this.getZ());
    }
}
