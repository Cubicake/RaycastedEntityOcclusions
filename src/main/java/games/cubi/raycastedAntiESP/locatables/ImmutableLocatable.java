package games.cubi.raycastedAntiESP.locatables;

import games.cubi.raycastedAntiESP.Logger;

import java.util.UUID;

public record ImmutableLocatable(UUID world, double x, double y, double z) implements Locatable {

    @Override
    public Locatable add(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

    @Override
    public Locatable subtract(Locatable locatable) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

    @Override
    public Locatable scalarMultiply(double factor) {
        Logger.errorAndReturn(new RuntimeException("Attempted to mutate an immutable BlockLocation"), 2);
        return null;
    }

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
