package games.cubi.raycastedantiesp.packetevents.locatables;

import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.locatables.minecraft.NettyTileEntity;
import games.cubi.raycastedantiesp.packetevents.replaydata.PacketEventsTileEntityReplayData;

public class PacketEventsTileEntity extends NettyTileEntity<PacketEventsTileEntityReplayData> {
    public PacketEventsTileEntity(ImmutableBlockLocatable location, boolean visible, int lastChecked) {
        super(location, visible, lastChecked);
    }
}
