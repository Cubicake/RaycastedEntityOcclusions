package games.cubi.locatables.implementations;

import games.cubi.locatables.MutableLocatable;

import java.util.UUID;

public class MutableLocatableImpl implements MutableLocatable {
    private double x;
    private double y;
    private double z;
    private UUID world;

    public MutableLocatableImpl(UUID world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public LocatableType getType() {
        return LocatableType.Mutable;
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
    public MutableLocatable setX(double x) {
        this.x = x;
        return this;
    }

    @Override
    public MutableLocatable setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public MutableLocatable setZ(double z) {
        this.z = z;
        return this;
    }

    public MutableLocatable setWorld(UUID world) {
        this.world = world;
        return this;
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
