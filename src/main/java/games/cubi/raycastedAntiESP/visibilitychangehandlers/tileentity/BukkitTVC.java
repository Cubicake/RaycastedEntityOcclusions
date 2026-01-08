package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BukkitTVC extends TileEntityCache implements TileEntityVisibilityChanger {
    @Override
    public void showTileEntityToPlayer(UUID player, BlockLocation tileEntity) {
        setTileEntityVisibilityForPlayer(player, tileEntity, true);
    }

    @Override
    public void hideTileEntityFromPlayer(UUID player, BlockLocation tileEntity) {
        setTileEntityVisibilityForPlayer(player, tileEntity, false);
    }

    @Override
    public void setTileEntityVisibilityForPlayer(UUID player, BlockLocation tileEntity, boolean visible) {
        if (SnapshotManager.getTileEntitySnapshotManager().isTileEntityVisibleToPlayer(tileEntity, player) == visible) {
            return;
        }
        addToTileEntityCache(player, tileEntity, visible);
    }

    @Override
    public VisibilityChangeHandlers.TileEntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT;
    }

    @Override
    public void processCache() {
        processACache(flushTileEntityShowCache(), true);
        processACache(flushTileEntityHideCache(), false);
    }

    private void processACache(Map<UUID, Set<AbstractBlockLocation>> cache, boolean show) {
        for (Map.Entry<UUID, Set<AbstractBlockLocation>> entry : cache.entrySet()) {
            UUID playerUUID = entry.getKey();
            Set<AbstractBlockLocation> tileEntities = entry.getValue();
            for (AbstractBlockLocation tileEntity : tileEntities) {
                BlockState blockState = Bukkit.getWorld(tileEntity.world()).getBlockState(tileEntity.blockX(), tileEntity.blockY(), tileEntity.blockZ());

                if (blockState instanceof TileState tileState) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) return;
                    Location location = blockState.getLocation();

                    BlockData blockData;
                    if (show) {
                        blockData = tileState.getBlockData();
                    }
                    else {
                        if (location.getBlockY() > 0) blockData = Material.STONE.createBlockData();
                        else blockData = Material.DEEPSLATE.createBlockData();
                    }

                    player.sendBlockChange(location, blockData);
                    if (show) player.sendBlockUpdate(location, tileState);
                }
                else {
                    //Logger.warning("Tried to show tile entity at " + location + " to "+p.getName()+" but it was not a TileState! Block type: " + block.getType()+". Removing from the list of tile entities.");
                    SnapshotManager.getTileEntitySnapshotManager().removeFromTileEntityLastSeenMap(tileEntity);
                }
            }
        }
    }
}
