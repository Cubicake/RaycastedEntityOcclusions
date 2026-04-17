package games.cubi.raycastedantiesp.packetevents.replaydata;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;

public final class PacketEventsTileEntityReplayData {
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
}
