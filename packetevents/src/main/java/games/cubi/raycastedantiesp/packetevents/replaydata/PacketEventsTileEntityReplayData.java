package games.cubi.raycastedantiesp.packetevents.replaydata;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import games.cubi.raycastedantiesp.core.utils.Clearable;

public final class PacketEventsTileEntityReplayData implements Clearable {
    private volatile BlockEntityType blockEntityType;
    private volatile NBTCompound nbt;

    public BlockEntityType blockEntityType() {
        return blockEntityType;
    }

    public NBTCompound nbt() {
        return nbt == null ? null : nbt.copy();
    }

    public void setBlockEntityData(BlockEntityType blockEntityType, NBTCompound nbt) {
        this.blockEntityType = blockEntityType;
        this.nbt = nbt == null ? null : nbt.copy();
    }

    @Override
    public void clear() {
        blockEntityType = null;
        nbt = null;
    }
}
