package games.cubi.raycastedAntiESP.locatables;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class LocatableImpl implements Locatable {
    private double x;
    private double y;
    private double z;
    private final UUID world;

    public LocatableImpl(UUID world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public LocatableType getType() {
        return LocatableType.Plain;
    }

    @Override
    public Locatable add(Locatable locatable) {
        this.x += locatable.x();
        this.y += locatable.y();
        this.z += locatable.z();
        return this;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        this.x -= locatable.x();
        this.y -= locatable.y();
        this.z -= locatable.z();
        return this;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        return this;
    }

    @Override
    public UUID world() {
        return world;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public boolean isFine() {
        return true;
    }

    @Override
    public Position offset(int x, int y, int z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public FinePosition offset(double x, double y, double z) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public BlockPosition toBlock() {
        throw new RuntimeException("Unimplemented");
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
    public String toString() {
        return toStringForm();
    }
}
