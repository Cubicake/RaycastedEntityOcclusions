package games.cubi.locatables.minecraft;

import games.cubi.locatables.implementations.ImmutableBlockLocatable;

public abstract class NettyTileEntity<PacketReplayData> implements TileEntityLocatable<PacketReplayData> {
    private final ImmutableBlockLocatable delegate;
    private volatile boolean visible;
    private volatile int lastChecked;
    private volatile int blockID;
    private volatile PacketReplayData extraData;

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
    public TileEntityLocatable<PacketReplayData> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public int lastChecked() {
        return lastChecked;
    }

    @Override
    public TileEntityLocatable<PacketReplayData> setLastChecked(int lastChecked) {
        this.lastChecked = lastChecked;
        return this;
    }

    @Override
    public int blockID() {
        return blockID;
    }

    @Override
    public TileEntityLocatable<PacketReplayData> setBlockID(int blockID) {
        this.blockID = blockID;
        return this;
    }

    @Override
    public PacketReplayData extraData() {
        return extraData;
    }

    @Override
    public TileEntityLocatable<PacketReplayData> setExtraData(PacketReplayData extraData) {
        this.extraData = extraData;
        return this;
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