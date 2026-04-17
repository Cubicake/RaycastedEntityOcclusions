package games.cubi.locatables;

public interface TileEntityLocatable<T> extends BlockLocatable {
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
