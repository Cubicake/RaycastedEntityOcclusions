package games.cubi.raycastedAntiESP.locatables;

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
