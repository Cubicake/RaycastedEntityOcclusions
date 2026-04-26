package games.cubi.raycastedantiesp.paper.staging;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.packetevents.BlockInfoResolver;
import games.cubi.raycastedantiesp.packetevents.config.PacketEventsBlockProcessorConfig;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PacketEventsPaperBlockInfoResolver implements BlockInfoResolver {
    private final boolean[] occlusionArray;
    private final boolean[] tileEntityArray;

    public static PacketEventsPaperBlockInfoResolver get;

    public PacketEventsPaperBlockInfoResolver() {
        get = this;
        boolean[][] result = iterateBlockIDs(false);
        occlusionArray = result[0];
        tileEntityArray = result[1];
        PacketEventsBlockProcessorConfig config = RaycastedAntiESP.getConfigManager().getExtensionConfig(PacketEventsBlockProcessorConfig.class);
        if (config != null) {
            for (int blockStateId : config.tileEntityExemptedIds()) {
                if (blockStateId >= 0 && blockStateId < tileEntityArray.length) {
                    tileEntityArray[blockStateId] = false;
                }
            }
            for (int blockStateId : config.tileEntityForceIncludedIds()) {
                if (blockStateId >= 0 && blockStateId < tileEntityArray.length) {
                    tileEntityArray[blockStateId] = true;
                }
            }
        }
    }

    /**
     * @return Boolean array with two nested boolean arrays. <code>boolean[0]</code> returns the occlusion status array, <code>boolean[1]</code> returns the tile entity status array. Both arrays are indexed by block state ID. Air blocks and invalid IDs are treated as non-occluding and non-tile-entity, and trailing air IDs are ignored to save memory.
     */
    public boolean[][] iterateBlockIDs(boolean materialToIDMode) {
        boolean run = true;
        int airs = 0;
        int lastNonAirID = 0;
        Map<Integer, Boolean> occlusion = new HashMap<>(111000); //Tests show 30,000 block IDs in 1.21.11, and we scan forwards for 80k air ids just in case, so 111k is enough. This is a pointless micro optimization but why not
        Map<Integer, Boolean> tileEntity = new HashMap<>(111000);
        int iterator = 0;
        while (run) {
            Material material = SpigotReflectionUtil.getBlockDataByCombinedId(iterator).getItemType(); //Unfortunately doesn't seem to be a way to do this without both using an internal PE api and the deprecated for removal MaterialData
            if (material == null) {
                Logger.warning("Material for block state ID " + iterator + " is null, stopping iteration. This is not expected to happen.", 5, PacketEventsPaperBlockInfoResolver.class);
                run = false;
                continue;
            }
            if (material == Material.AIR) {
                airs++;
                if (airs > 80000) { // There is a sequence of ~40 air blocks around ID 100, and another of several hundred at ~3000. We scan forwards 80k to future-proof any mojank. Since it runs once at startup, perf is irrelevant here
                    run = false;
                    continue;
                }
            }
            else {
                airs = 0;
                lastNonAirID = iterator;
                if (materialToIDMode) {
                    Logger.debug(material.toString() + iterator);
                }
            }

            occlusion.put(iterator, material.isOccluding());
            try {
                BlockData data = material.createBlockData();
                if (data.createBlockState() instanceof TileState) {
                    //Logger.debug("tile at" + iterator + " is tile entity" + material.name());
                    tileEntity.put(iterator, true);
                } else {
                    tileEntity.put(iterator, false);
                }
            } catch (Exception a) {
                tileEntity.put(iterator, false);
                // will sometimes inconsistently happen, just ignore it ig?
            }
            iterator++;
        }
        //Logger.debug(iterator+" was the finish point, last non-air ID was "+lastNonAirID);
        boolean[][] result = new boolean[2][lastNonAirID + 1];
        for (int i = 0; i < (lastNonAirID + 1) /*Ignore the trailing airs*/; i++) {
            result[0][i] = occlusion.get(i);
            result[1][i] = tileEntity.get(i);
            //Logger.debug("BlockState ID " + i + ": occluding=" + occlusionArray[i] + ", tileEntity=" + tileEntityArray[i]);
        }
        return result;
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
