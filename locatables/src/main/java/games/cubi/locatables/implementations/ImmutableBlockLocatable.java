package games.cubi.locatables.implementations;

import games.cubi.locatables.ImmutableLocatable;
import games.cubi.locatables.BlockLocatable;

import java.util.UUID;

public record ImmutableBlockLocatable(UUID world, int blockX, int blockY, int blockZ) implements BlockLocatable, ImmutableLocatable {

    public ImmutableBlockLocatable(UUID world, double x, double y, double z) {
        this(world, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    @Override
    public LocatableType getType() {
        return LocatableType.ImmutableBlockLocation;
    }

    @Override
    public boolean equals(Object o) {
        return isEqual(o);
    }

    @Override
    public int hashCode() {
        return hash();
    }

    @Override
    public String toString() {
        return toStringForm();
    }
}
