package games.cubi.raycastedantiesp.packetevents.locatables;

import games.cubi.locatables.Locatable;
import games.cubi.raycastedantiesp.core.locatables.NettyTileEntity;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsTileEntityReplayData;

import java.util.UUID;

public class PacketEventsTileEntity extends NettyTileEntity<PacketEventsTileEntityReplayData> {
    public PacketEventsTileEntity(Locatable location, boolean visible, int lastChecked, int blockID) {
        super(location, visible, lastChecked, blockID);
    }

    public PacketEventsTileEntity(UUID world, int x, int y, int z, boolean visible, int blockID) {
        super(world, x, y, z, visible, blockID);
    }

    @Override
    public boolean strictlyEquals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PacketEventsTileEntity that)) return false;
        if (!this.equals(that)) return false;
        return this.visible() == that.visible() &&
                this.lastChecked() == that.lastChecked() &&
                this.blockID() == that.blockID() &&
                ((this.extraData() == null && that.extraData() == null) || (this.extraData() != null && this.extraData().equals(that.extraData())));
    }
}
