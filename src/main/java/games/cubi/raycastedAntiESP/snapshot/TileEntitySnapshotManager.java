package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.utils.BlockLocation;
import games.cubi.raycastedAntiESP.utils.Locatable;

import org.bukkit.World;

import java.util.Set;
import java.util.UUID;

public interface TileEntitySnapshotManager {
    public Set<UUID> getPlayersWhoCanSee(Locatable tileEntity);
    public Set<BlockLocation> getTileEntitiesInChunk(World world, int x, int z);
}
