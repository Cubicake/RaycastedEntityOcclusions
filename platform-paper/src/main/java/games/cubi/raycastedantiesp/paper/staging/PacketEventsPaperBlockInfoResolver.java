package games.cubi.raycastedantiesp.paper.staging;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.packetevents.BlockInfoResolver;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

public class PacketEventsPaperBlockInfoResolver implements BlockInfoResolver {
    private boolean[] occlusionArray;
    private boolean[] tileEntityArray;

    public static PacketEventsPaperBlockInfoResolver get;

    public PacketEventsPaperBlockInfoResolver() {
        get = this;
        Bukkit.getScheduler().runTaskLater(RaycastedAntiESP.get(), this::initialize, 5);
    }

    private void initialize() {
        boolean run = true;
        int airs = 0;
        Map<Integer, Boolean> occlusion = new HashMap<>(3775); //500 more than max capacity that was observed using this system on 1.21.11, however it will vary with versions and will increase as blocks are added. Doesn't really matter anyways, this is computed once
        Map<Integer, Boolean> tileEntity = new HashMap<>(3775);
        int iterator = 0;
        while (run) {
            Material material = SpigotReflectionUtil.getBlockDataByCombinedId(iterator).getItemType(); //Unfortunately doesn't seem to be a way to do this without both using an internal PE api and the deprecated for removal MaterialData
            if (material == null) {
                run = false;
                continue;
            }
            if (material == Material.AIR) {
                airs++;
                if (airs > 100) { // There is a sequence of ~40 air blocks around ID 100, so 100 should be a safe number which never fails even if mojang does some weird stuff
                    run = false;
                    continue;
                }
            }
            else {
                airs = 0;
            }
            occlusion.put(iterator, material.isOccluding());
            BlockData data = material.createBlockData();
            if (data != null && data.createBlockState() instanceof TileState) {
                tileEntity.put(iterator, true);
            } else {
                tileEntity.put(iterator, false);
            }
            iterator++;
        }
        occlusionArray = new boolean[occlusion.size()];
        tileEntityArray = new boolean[tileEntity.size()];
        for (int i = 0; i < occlusion.size() - 95 /*Ignore the trailing airs*/; i++) {
            occlusionArray[i] = occlusion.get(i);
            tileEntityArray[i] = tileEntity.get(i);
            Logger.debug("BlockState ID " + i + ": occluding=" + occlusionArray[i] + ", tileEntity=" + tileEntityArray[i]);
        }
        for (int j = 0; j < occlusionArray.length; j++) {
            Logger.debug("ID " + j + ": occludingArray=" + occlusionArray[j] + ", occludingMap=" + occlusion.get(j));
            if (occlusionArray[j] != occlusion.get(j)) {
                Logger.warning("Mismatch at ID " + j + ": occlusionArray=" + occlusionArray[j] + ", occlusionMap=" + occlusion.get(j), 3);
            }
        }
    }

    @Override
    public boolean isOccluding(int blockStateID) {
        if (blockStateID < 0 || blockStateID >= occlusionArray.length) {
            return false; // Default to non-occluding for invalid IDs, should be safe since invalid IDs shouldn't exist in the world
        }
        return occlusionArray[blockStateID];
    }

    @Override
    public boolean isTileEntity(int blockStateID) {
        if (blockStateID < 0 || blockStateID >= tileEntityArray.length) {
            return false; // Default to non-tile-entity for invalid IDs, should be safe since invalid IDs shouldn't exist in the world
        }
        return tileEntityArray[blockStateID];
    }

    public boolean[] dumpOcclusionArray() {
        return occlusionArray;
    }

    private boolean[] dumpTileEntityArray() {
        return tileEntityArray;
    }
}
