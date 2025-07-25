package games.cubi.raycastedEntityOcclusion.Snapshot;

import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;

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
    public static class Data {
        public final ChunkSnapshot snapshot;
        public final ConcurrentHashMap<Location, Material> delta = new ConcurrentHashMap<>();
        public final Set<Location> tileEntities = ConcurrentHashMap.newKeySet();
        public long lastRefresh;
        public int minHeight;
        public int maxHeight;

        public Data(ChunkSnapshot snapshot, long time) {
            this.snapshot = snapshot;
            this.lastRefresh = time;
        }
    }

    private final ConcurrentHashMap<String, Data> dataMap = new ConcurrentHashMap<>();
    private final ConfigManager cfg;
    private final RaycastedEntityOcclusion plugin;

    public ChunkSnapshotManager(RaycastedEntityOcclusion plugin) {
        cfg = plugin.getConfigManager();
        this.plugin = plugin;
        //get loaded chunks and add them to dataMap
        for (World w : plugin.getServer().getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis()));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                int chunksRefreshed = 0;
                int chunksToRefreshMaximum = getNumberOfCachedChunks() / 3;
                for (Map.Entry<String, Data> e : dataMap.entrySet()) {
                    if (now - e.getValue().lastRefresh >= cfg.snapshotRefreshInterval * 1000L && chunksRefreshed < chunksToRefreshMaximum) {
                        chunksRefreshed++;
                        String key = e.getKey();
                        //String[] parts = key.split(":");
                        Chunk c = getKeyChunk(key);
                        e.setValue(takeSnapshot(c, now));
                    }
                }
                if (cfg.debugMode) {
                    plugin.getLogger().info("ChunkSnapshotManager: Refreshed " + chunksRefreshed + " chunks out of " + chunksToRefreshMaximum + " maximum.");
                }
            }
        }.runTaskTimer(plugin, cfg.snapshotRefreshInterval * 2L, cfg.snapshotRefreshInterval * 2L /* This runs 10 times per refreshInterval, spreading out the refreshes */);
    }

    public void onChunkLoad(Chunk c) {
        dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis()));
    }

    public void onChunkUnload(Chunk c) {
        dataMap.remove(key(c));
    }

    // Used by EventListener to update the delta map when a block is placed or broken
    public void onBlockChange(Location loc, Material m) {
        if (cfg.debugMode) {
            plugin.getLogger().info("ChunkSnapshotManager: Block change at " + loc + " to " + m);
        }
        Data d = dataMap.get(key(loc.getChunk()));
        if (d != null) {
            d.delta.put(loc, m);
            if (cfg.checkTileEntities) {
                // Check if the block is a tile entity
                BlockState data = loc.getBlock().getState();
                loc = loc.clone().add(0.5, 0.5, 0.5);
                if (data instanceof TileState) {
                    if (cfg.debugMode){
                        plugin.getLogger().info("ChunkSnapshotManager: Tile entity at " + loc);
                    }
                    d.tileEntities.add(loc);
                } else {
                    d.tileEntities.remove(loc);
                }
            }
        }
    }

    private Data takeSnapshot(Chunk c, long now) {
        World w = c.getWorld();
        Data data = new Data(c.getChunkSnapshot(), now);
        int chunkX = c.getX() * 16;
        int chunkZ = c.getZ() * 16;
        int minHeight = w.getMinHeight();
        int maxHeight = w.getMaxHeight();
        data.maxHeight = maxHeight;
        data.minHeight = minHeight;
        if (cfg.checkTileEntities) {
            for (int x = 0; x < 16; x++) {
                for (int y = minHeight; y < maxHeight; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState bs = data.snapshot.getBlockData(x, y, z).createBlockState();

                        if (bs instanceof TileState) {
                            data.tileEntities.add(new Location(w, x+ chunkX +0.5, y+0.5, z + chunkZ+0.5));
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
        return world + ":" + x + ":" + z;
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
        Data d = dataMap.get(key(loc.getChunk()));
        if (d == null) {
            Chunk c = loc.getChunk();
            //dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis())); infinite loop
            System.err.println("ChunkSnapshotManager: No snapshot for " + c+ " Please report this on our discord (discord.cubi.games)'");
            return loc.getBlock().getType();
        }
        double yLevel = loc.getY();
        if (yLevel < d.minHeight || yLevel > d.maxHeight) {
            return null;
        }
        Material dm = d.delta.get(loc);
        if (dm != null) {
            return dm;
        }
        int x = loc.getBlockX() & 0xF;
        int y = loc.getBlockY();
        int z = loc.getBlockZ() & 0xF;

        return d.snapshot.getBlockType(x, y, z);
    }

    //get TileEntity Locations in chunk
    public Set<Location> getTileEntitiesInChunk(World world, int x, int z) {
        Data d = dataMap.get(key(world, x, z));
        if (d == null) {
            return Collections.emptySet();
        }
        return d.tileEntities;
    }

    public int getNumberOfCachedChunks() {
        return dataMap.size();
        //created to use in a debug command maybe
    }

}
