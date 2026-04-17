package games.cubi.locatables.minecraft;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.ImmutableLocatable;
import games.cubi.locatables.MutableLocatable;

public interface TileEntityLocatable<T> extends BlockLocatable, ImmutableLocatable {
    boolean visible();
    TileEntityLocatable<T> setVisible(boolean visible);

    int lastChecked();
    TileEntityLocatable<T> setLastChecked(int lastChecked);

    int blockID();
    TileEntityLocatable<T> setBlockID(int blockID);

    T extraData();
    TileEntityLocatable<T> setExtraData(T extraData);

    @Override
    default boolean isMutable() {
        return false;
    }

    @Override
    default MutableLocatable castToMutableOrNull() {
        return null;
    }
}
