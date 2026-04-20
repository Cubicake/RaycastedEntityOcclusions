package games.cubi.locatables.implementations;

import games.cubi.locatables.ImmutableLocatable;

import java.util.UUID;

public record ImmutableLocatableImpl(UUID world, double x, double y, double z) implements ImmutableLocatable {

    @Override
    public LocatableType getType() {
        return LocatableType.Immutable;
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

    @Override
    public boolean strictlyEquals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ImmutableLocatableImpl(UUID world1, double x1, double y1, double z1))) return false;
        return this.world.equals(world1) &&
                Double.doubleToLongBits(this.x) == Double.doubleToLongBits(x1) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(y1) &&
                Double.doubleToLongBits(this.z) == Double.doubleToLongBits(z1);
    }
}
