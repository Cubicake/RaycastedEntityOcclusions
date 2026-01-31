package games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.AbstractBlockLocation;
import games.cubi.raycastedAntiESP.locatables.block.BlockLocation;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import net.kyori.adventure.util.TriState;
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
        if (SnapshotManager.getTileEntitySnapshotManager().isTileEntityVisibleToPlayer(tileEntity, player).equals(TriState.TRUE)) {
            return; // Already visible
        }

        addToTileEntityCache(player, tileEntity);
    }

    BlockData STONE_DATA = Material.STONE.createBlockData();
    BlockData DEEPSLATE_DATA = Material.DEEPSLATE.createBlockData();

    @Override
    public void hideTileEntityFromPlayer(UUID player, BlockLocation tileEntity) {
        if (SnapshotManager.getTileEntitySnapshotManager().isTileEntityVisibleToPlayer(tileEntity, player).equals(TriState.FALSE)) {
            return; // Already hidden
        }

        if (tileEntity.blockY() > 0) Bukkit.getPlayer(player).sendBlockChange(tileEntity.toBukkitLocation(), STONE_DATA);
        else {
            Player p = Bukkit.getPlayer(player);
            Location loc = tileEntity.toBukkitLocation().toBlockLocation();

            p.sendBlockChange(loc, DEEPSLATE_DATA);
            Logger.debug("Sent deepslate block change to " + p.getName() + " at " + loc);
        } //todo need to toggle isTileEntityVisibleToPlayer in snapshot manager
    }

    @Override
    public VisibilityChangeHandlers.TileEntityVisibilityChangerType getType() {
        return VisibilityChangeHandlers.TileEntityVisibilityChangerType.BUKKIT;
    }

    @Override
    public void processCache() {
        for (Map.Entry<UUID, Set<AbstractBlockLocation>> entry : getCache().entrySet()) {
            UUID playerUUID = entry.getKey();
            Set<AbstractBlockLocation> tileEntities = entry.getValue();
            for (AbstractBlockLocation tileEntity : tileEntities) {
                BlockState blockState = Bukkit.getWorld(tileEntity.world()).getBlockState(tileEntity.blockX(), tileEntity.blockY(), tileEntity.blockZ());

                if (blockState instanceof TileState tileState) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) return;
                    Location location = blockState.getLocation();

                    player.sendBlockChange(location, tileState.getBlockData());
                    player.sendBlockUpdate(location, tileState);
                }
                else {
                    //Logger.warning("Tried to show tile entity at " + location + " to "+p.getName()+" but it was not a TileState! Block type: " + block.getType()+". Removing from the list of tile entities.");
                    SnapshotManager.getTileEntitySnapshotManager().removeFromTileEntityLastSeenMap(tileEntity);
                }
            }
        }
    }
}
