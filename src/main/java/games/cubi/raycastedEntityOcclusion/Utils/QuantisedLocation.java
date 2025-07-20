package games.cubi.raycastedEntityOcclusion.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public class QuantisedLocation {
    // One decimal place quantisation. This means that the coordinates are rounded to the nearest 0.1 block, and since integers are used this works up to 200m blocks away (integer limit/10)
    // Note that the last digit of the integer is the tenths place, so 123456789 represents 12345678.9 in real coordinates.
    private final int x;
    private final int y;
    private final int z;
    private final UUID world;

    public QuantisedLocation(Location location) {
        this.x = (int) Math.floor(location.getX() * 10);
        this.y = (int) Math.floor(location.getY() * 10);
        this.z = (int) Math.floor(location.getZ() * 10);
        this.world = location.getWorld().getUID();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x / 10.0, y / 10.0, z / 10.0);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public UUID world() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuantisedLocation q)) return false;
        return x == q.x && y == q.y && z == q.z && world.equals(q.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

}
