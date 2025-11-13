package games.cubi.raycastedAntiESP.utils;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WrappedBukkitLocation extends Location implements Locatable{
    public WrappedBukkitLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public WrappedBukkitLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public static WrappedBukkitLocation wrap(Location location) {
        if (location instanceof WrappedBukkitLocation location1) {
            return location1;
        }
        return new WrappedBukkitLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
    public UUID world() {
        return super.getWorld().getUID();
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
}
