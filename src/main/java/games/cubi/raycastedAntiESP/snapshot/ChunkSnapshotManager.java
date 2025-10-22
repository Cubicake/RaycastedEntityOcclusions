package games.cubi.raycastedAntiESP.snapshot;

import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;

import games.cubi.raycastedAntiESP.utils.BlockLocation;
import games.cubi.raycastedAntiESP.utils.Enums;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkSnapshotManager {
    private static class ChunkData {
        public final ChunkSnapshot snapshot;
        public final ConcurrentHashMap<BlockLocation, Material> delta = new ConcurrentHashMap<>();
        public final Set<BlockLocation> tileEntities = ConcurrentHashMap.newKeySet();
        public long lastRefresh;
        public int minHeight;
        public int maxHeight;

        public ChunkData(ChunkSnapshot snapshot, long time) {
            this.snapshot = snapshot;
            this.lastRefresh = time;
        }
    }

    private static final ConcurrentHashMap<String, ChunkData> dataMap = new ConcurrentHashMap<>();
    private final ConfigManager cfg;
    private final RaycastedAntiESP plugin;

    public ChunkSnapshotManager(RaycastedAntiESP plugin, ConfigManager config) {
        cfg = config;
        this.plugin = plugin;
        //get loaded chunks and add them to dataMap
        for (World w : plugin.getServer().getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                snapshotChunk(c);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                int chunksRefreshed = 0;
                int chunksToRefreshMaximum = getNumberOfCachedChunks() / 3;
                for (Map.Entry<String, ChunkData> e : dataMap.entrySet()) {
                    if (now - e.getValue().lastRefresh >= cfg.getSnapshotConfig().getWorldSnapshotRefreshInterval() * 1000L && chunksRefreshed < chunksToRefreshMaximum) {
                        chunksRefreshed++;
                        String key = e.getKey();
                        snapshotChunk(key);
                    }
                }
                Logger.info("ChunkSnapshotManager: Refreshed " + chunksRefreshed + " chunks out of " + chunksToRefreshMaximum + " maximum.", 10);
            }
        }.runTaskTimerAsynchronously(plugin, cfg.getSnapshotConfig().getWorldSnapshotRefreshInterval() * 2L, cfg.getSnapshotConfig().getWorldSnapshotRefreshInterval() * 2L /* This runs 10 times per refreshInterval, spreading out the refreshes */);
    }

    public void onChunkLoad(Chunk c) {
        snapshotChunk(c);
    }

    public void onChunkUnload(Chunk c) {
        removeChunkSnapshot(c);
    }

    public void snapshotChunk(Chunk c) {
        Logger.info("ChunkSnapshotManager: Taking snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ(),10);
        dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis()));
    }
    public void snapshotChunk(String key) {
        snapshotChunk(getKeyChunk(key));
    }
    public void removeChunkSnapshot(Chunk c) {
        Logger.info("ChunkSnapshotManager: Removing snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ(), 9);
        dataMap.remove(key(c));
    }

    // Used by EventListener to update the delta map when a block is placed or broken
    public void onBlockChange(Location loc, Material m, Enums.BlockChangeType type) {
        Logger.info("ChunkSnapshotManager: Block change at " + loc + " to " + m, 9);
        ChunkData d = dataMap.get(key(loc.getChunk()));
        if (d == null) {
            Logger.error("Data map value empty, ignoring block update!",2);
            return;
        }
        BlockLocation location = new BlockLocation(loc);
        d.delta.put(location, m);
        if (cfg.getTileEntityConfig().isEnabled()) {
            // Check if the block is a tile entity
            BlockState data = loc.getBlock().getState();
            loc = loc.clone().add(0.5, 0.5, 0.5);
            if (data instanceof TileState) {
                Logger.info("ChunkSnapshotManager: Tile entity at " + loc, 8);
                if (type == Enums.BlockChangeType.PLACED) {
                    d.tileEntities.add(location);
                } else if (type == Enums.BlockChangeType.BROKEN) {
                    d.tileEntities.remove(location);
                }
            }
        }
        else {Logger.error("Data map value empty, ignoring block update!", 2);}
    }

    private ChunkData takeSnapshot(Chunk c, long now) {
        World w = c.getWorld();
        ChunkData data = new ChunkData(c.getChunkSnapshot(), now);
        int chunkX = c.getX() * 16;
        int chunkZ = c.getZ() * 16;
        int minHeight = w.getMinHeight();
        int maxHeight = w.getMaxHeight();
        data.maxHeight = maxHeight;
        data.minHeight = minHeight;
        if (cfg.getTileEntityConfig().isEnabled()) {
            for (int x = 0; x < 16; x++) {
                for (int y = minHeight; y < maxHeight; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState bs = data.snapshot.getBlockData(x, y, z).createBlockState();

                        if (bs instanceof TileState) {
                            data.tileEntities.add(new BlockLocation(w, x+ chunkX, y, z + chunkZ));
                        }
                    }
                }
            }
        }
        return data;
    }

    public String key(Chunk c) {
        return c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ();
    }
    public String key(World world, int x, int z) {
        return world.getName() + ":" + x + ":" + z;
    }
    public int getKeyX(String key) {
        String[] parts = key.split(":");
        return Integer.parseInt(parts[1]);
    }
    public int getKeyZ(String key) {
        String[] parts = key.split(":");
        return Integer.parseInt(parts[2]);
    }
    public World getKeyWorld(String key) {
        String[] parts = key.split(":");
        return Bukkit.getWorld(parts[0]);
    }
    public Chunk getKeyChunk(String key) {
        return getKeyWorld(key).getChunkAt(getKeyX(key), getKeyZ(key));
    }

    public Material getMaterialAt(Location loc) {
        Chunk chunk = loc.getChunk();
        ChunkData d = dataMap.get(key(chunk));
        if (d == null) {
            Chunk c = loc.getChunk();
            Logger.error("ChunkSnapshotManager: No snapshot for " + c+ " If this error persists, please report this on our discord (discord.cubi.games)", 3);
            //EngineOld.syncRecheck.add(chunk); TODO reenable
            return loc.getBlock().getType();
        }
        double yLevel = loc.getY();
        if (yLevel < d.minHeight || yLevel > d.maxHeight) {
            return null;
        }
        Material dm = d.delta.get(new BlockLocation(loc));
        if (dm != null) {
            Logger.info("Using delta", 9);
            return dm;
        }
        int x = loc.getBlockX() & 0xF;
        int y = loc.getBlockY();
        int z = loc.getBlockZ() & 0xF;

        return d.snapshot.getBlockType(x, y, z);
    }

    //get TileEntity Locations in chunk
    public Set<Location> getTileEntitiesInChunk(World world, int x, int z) {
        ChunkData d = dataMap.get(key(world, x, z));
        if (d == null) {
            return Collections.emptySet();
        }
        return BlockLocation.toCentredLocations(d.tileEntities);
    }

    public int getNumberOfCachedChunks() {
        return dataMap.size();
        //created to use in a info command maybe
    }

}
