package games.cubi.raycastedantiesp.core.locatables;

import games.cubi.locatables.Locatable;
import games.cubi.raycastedantiesp.core.utils.Clearable;

import java.util.UUID;

public abstract class NettyTileEntity<PacketReplayData extends Clearable> implements TileEntityLocatable<PacketReplayData> {
    private volatile boolean visible;
    private volatile int lastChecked;
    private final int blockID;
    private volatile PacketReplayData extraData;

    private final int x, y, z;
    private final UUID world;

    public NettyTileEntity(Locatable location, boolean visible, int lastChecked, int blockID) {
        x = location.blockX();
        y = location.blockY();
        z = location.blockZ();
        world = location.world();

        this.blockID = blockID;

        this.visible = visible;
        this.lastChecked = lastChecked;
    }

    public NettyTileEntity(UUID world, int x, int y, int z, boolean visible, int blockID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.visible = visible;
        this.blockID = blockID;
        this.world = world;
        lastChecked = 0;
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
        return x;
    }

    @Override
    public int blockY() {
        return y;
    }

    @Override
    public int blockZ() {
        return z;
    }

    @Override
    public LocatableType getType() {
        return LocatableType.NettyTileEntity;
    }

    @Override
    public UUID world() {
        return world;
    }

    @Override
    public void clear() {
        if (extraData != null) {
            extraData.clear();
        }
    }

    @Override
    public boolean equals(Object other) {
        return isEqualTo(other);
    }

    @Override
    public int hashCode() {
        return blockHash();
    }

    @Override
    public String toString() {
        return toStringForm();
    }
}
