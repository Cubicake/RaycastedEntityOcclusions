package games.cubi.raycastedAntiESP.locatables;

import games.cubi.raycastedAntiESP.Logger;

import java.util.UUID;

public record ImmutableLocatableImpl(UUID world, double x, double y, double z) implements Locatable {

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
}
