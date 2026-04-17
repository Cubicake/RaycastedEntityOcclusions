package games.cubi.locatables.implementations;

import games.cubi.locatables.ImmutableLocatable;
import games.cubi.locatables.MutableLocatable;
import games.cubi.locatables.TileEntityLocatable;

public class NettyTileEntity implements TileEntityLocatable<Object>, ImmutableLocatable {
    private final ImmutableBlockLocatable delegate;
    private volatile boolean visible;
    private volatile int lastChecked;
    private volatile int blockID;
    private volatile Object extraData;

    public NettyTileEntity(ImmutableBlockLocatable location, boolean visible, int lastChecked) {
        this.delegate = location;
        this.visible = visible;
        this.lastChecked = lastChecked;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public TileEntityLocatable<Object> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public int lastChecked() {
        return lastChecked;
    }

    @Override
    public TileEntityLocatable<Object> setLastChecked(int lastChecked) {
        this.lastChecked = lastChecked;
        return this;
    }

    @Override
    public int blockID() {
        return blockID;
    }

    @Override
    public TileEntityLocatable<Object> setBlockID(int blockID) {
        this.blockID = blockID;
        return this;
    }

    @Override
    public Object extraData() {
        return extraData;
    }

    @Override
    public TileEntityLocatable<Object> setExtraData(Object extraData) {
        this.extraData = extraData;
        return this;
    }

    @Override
    public boolean isMutable() {
        return ImmutableLocatable.super.isMutable();
    }

    @Override
    public MutableLocatable castToMutableOrNull() {
        return ImmutableLocatable.super.castToMutableOrNull();
    }

    @Override
    public int blockX() {
        return delegate.blockX();
    }

    @Override
    public int blockY() {
        return delegate.blockY();
    }

    @Override
    public int blockZ() {
        return delegate.blockZ();
    }

    @Override
    public LocatableType getType() {
        return LocatableType.NettyTileEntity;
    }

    @Override
    public java.util.UUID world() {
        return delegate.world();
    }
}