package games.cubi.locatables;

import games.cubi.locatables.implementations.MutableLocatableImpl;

public non-sealed interface BlockLocatable extends Locatable {

    default MutableLocatable clonePlainAndCentreIfBlockLocation() {
        return new MutableLocatableImpl(world(), blockX() + 0.5, blockY() + 0.5, blockZ() + 0.5);
    }

    default boolean isEqual(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockLocatable other)) return false;
        return this.blockX() == other.blockX() && this.blockY() == other.blockY() && this.blockZ() == other.blockZ() && world().equals(other.world());
    }

    default int blockHash() {
        int result = 17;
        result = 31 * result + (this.world() != null ? this.world().hashCode() : 0);
        result = 31 * result + this.blockX();
        result = 31 * result + this.blockY();
        result = 31 * result + this.blockZ();
        return result;
    }

    @Override
    default double x() {
        return blockX();
    }

    @Override
    default double y() {
        return blockY();
    }

    @Override
    default double z() {
        return blockZ();
    }

    @Override
    int blockX();

    @Override
    int blockY();

    @Override
    int blockZ();
}
