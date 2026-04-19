package games.cubi.raycastedantiesp.packetevents.view;

import games.cubi.locatables.implementations.ImmutableBlockLocatable;
import games.cubi.raycastedantiesp.core.view.AbstractBlockView;
import games.cubi.raycastedantiesp.packetevents.locatables.PacketEventsTileEntity;

public class PacketEventsBlockView extends AbstractBlockView<PacketEventsTileEntity> {
    @Override
    protected PacketEventsTileEntity createTrackedTileEntity(ImmutableBlockLocatable location, int lastChecked) {
        return new PacketEventsTileEntity(location, false, lastChecked);
    }
}
