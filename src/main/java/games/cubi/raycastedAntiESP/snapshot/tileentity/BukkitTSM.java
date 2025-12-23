package games.cubi.raycastedAntiESP.snapshot.tileentity;

import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.block.BukkitBSM;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class BukkitTSM extends PlayerLastSeenTracker {
    public BukkitTSM() {
    }


    @Override
    public int getTicksSincePlayerSawTileEntity(UUID player, AbstractBlockLocation tileEntity) {
        //getPlayerLastSeenTimestamps(tileEntity).
        Set<PlayerLastCheckTimestamp> timestamps = getPlayerLastSeenTimestamps(tileEntity);
        for (PlayerLastCheckTimestamp playerLastCheckTimestamp : timestamps) {
            if (playerLastCheckTimestamp.getPlayer().equals(player)) {
                if (!playerLastCheckTimestamp.hasBeenSeen()) {
                    return -1;
                }
                return (DataHolder.getTick() - playerLastCheckTimestamp.getTimestamp());
            }
        }
        return -1;
    }

    @Override
    public Set<BlockLocation> getTileEntitiesInChunk(UUID world, int x, int z) {
        BukkitBSM bukkitBSM = (BukkitBSM) SnapshotManager.getBlockSnapshotManager();
        return bukkitBSM.getTileEntitiesInChunk(Bukkit.getWorld(world), x, z);
    }

    @Override
    public SnapshotManager.TileEntitySnapshotManagerType getType() {
        return SnapshotManager.TileEntitySnapshotManagerType.BUKKIT;
    }
}
