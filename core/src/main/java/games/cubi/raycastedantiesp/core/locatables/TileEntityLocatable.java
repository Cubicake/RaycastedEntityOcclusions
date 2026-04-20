package games.cubi.raycastedantiesp.core.locatables;

import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.ImmutableLocatable;
import games.cubi.locatables.MutableLocatable;
import games.cubi.raycastedantiesp.core.utils.Clearable;

public interface TileEntityLocatable<T> extends BlockLocatable, ImmutableLocatable, Clearable {
    boolean visible();
    TileEntityLocatable<T> setVisible(boolean visible);

    int lastChecked();
    TileEntityLocatable<T> setLastChecked(int lastChecked);

    int blockID();

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
