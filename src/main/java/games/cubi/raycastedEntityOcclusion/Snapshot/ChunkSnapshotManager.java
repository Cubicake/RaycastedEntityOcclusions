package games.cubi.raycastedEntityOcclusion.Snapshot;

import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.EventListener;
import games.cubi.raycastedEntityOcclusion.Logger;
import games.cubi.raycastedEntityOcclusion.Raycast.Engine;
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

import java.util.HashSet;
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

    public ChunkSnapshotManager(RaycastedEntityOcclusion plugin) {
        cfg = plugin.getConfigManager();
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
                    if (now - e.getValue().lastRefresh >= cfg.snapshotRefreshInterval * 1000L && chunksRefreshed < chunksToRefreshMaximum) {
                        chunksRefreshed++;
                        String key = e.getKey();
                        snapshotChunk(key);
                    }
                }
                if (cfg.debugMode) {
                    Logger.info("ChunkSnapshotManager: Refreshed " + chunksRefreshed + " chunks out of " + chunksToRefreshMaximum + " maximum.");
                }
            }
        }.runTaskTimerAsynchronously(plugin, cfg.snapshotRefreshInterval * 2L, cfg.snapshotRefreshInterval * 2L /* This runs 10 times per refreshInterval, spreading out the refreshes */);
    }

    public void onChunkLoad(Chunk c) {
        snapshotChunk(c);
    }

    public void onChunkUnload(Chunk c) {
        removeChunkSnapshot(c);
    }

    public void snapshotChunk(Chunk c) {
        //if (cfg.debugMode) {
            //Logger.info("ChunkSnapshotManager: Taking snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ());
        //}
        //run an async task copilot
        Bukkit.getScheduler().runTaskAsynchronously(RaycastedEntityOcclusion.getInstance(), () -> {
            dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis()));
        });
    }
    public void snapshotChunk(String key) {
        snapshotChunk(getKeyChunk(key));
    }
    public void removeChunkSnapshot(Chunk c) {
        if (cfg.debugMode) {
            Logger.info("ChunkSnapshotManager: Removing snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ());
        }
        dataMap.remove(key(c));
    }

    // Used by EventListener to update the delta map when a block is placed or broken
    public void onBlockChange(Location loc, Material m, int change) {
        if (cfg.debugMode) {
            Logger.info("ChunkSnapshotManager: Block change at " + loc + " to " + m);
        }
        ChunkData d = dataMap.get(key(loc.getChunk()));
        if (d != null) {
            d.delta.put(BlockLocation.fromLocation(loc), m);
            if (cfg.checkTileEntities) {
                // Check if the block is a tile entity
                BlockState data = loc.getBlock().getState();
                if (data instanceof TileState) {
                    if (cfg.debugMode) {
                        Logger.info("ChunkSnapshotManager: Tile entity at " + loc);
                    }
                    if (change == EventListener.PLACE) {
                        d.tileEntities.add(BlockLocation.fromLocation(loc));
                    }
                    if (change == EventListener.BREAK) {
                        d.tileEntities.remove(BlockLocation.fromLocation(loc));
                    }
                }
            }
        }
        else {Logger.error("Data map value empty, ignoring block update!");}
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
        if (cfg.checkTileEntities) {
            for (int x = 0; x < 16; x++) {
                for (int y = minHeight; y < maxHeight; y++) {
                    for (int z = 0; z < 16; z++) {
                        BlockState bs = data.snapshot.getBlockData(x, y, z).createBlockState();

                        if (bs instanceof TileState) {
                            data.tileEntities.add(BlockLocation.fromValues(w, x+ chunkX, y, z + chunkZ));
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
            Logger.error("ChunkSnapshotManager: No snapshot for " + c+ " If this error persists, please report this on our discord (discord.cubi.games)");
            Engine.syncRecheck.add(chunk);
            return loc.getBlock().getType();
        }
        double yLevel = loc.getY();
        if (yLevel < d.minHeight || yLevel > d.maxHeight) {
            return null;
        }
        Material dm = d.delta.get(BlockLocation.fromLocation(loc));
        if (dm != null) {
            if (cfg.debugMode) Logger.info("Using delta");
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
        return BlockLocation.toLocations(d.tileEntities);
    }

    public void removeTileEntity(Location loc) {
        Chunk c = loc.getChunk();
        ChunkData d = dataMap.get(key(c));
        if (d != null) {
            d.tileEntities.remove(BlockLocation.fromLocation(loc));
            if (cfg.debugMode) {
                Logger.info("ChunkSnapshotManager: Removed tile entity at " + loc);
            }
        } else {
            Logger.error("ChunkSnapshotManager: No snapshot for " + c + " when removing tile entity at " + loc);
        }
    }

    public int getNumberOfCachedChunks() {
        return dataMap.size();
        //created to use in a debug command maybe
    }

    private static class BlockLocation {
        private final Location location;
        private final int hashCode;

        private BlockLocation(Location location) {
            this.location = location.toCenterLocation();
            this.location.setPitch(0);
            this.location.setYaw(0);
            hashCode = calculateHashCode();
        }

        static BlockLocation fromLocation(Location loc) {
            return new BlockLocation(loc);
        }

        static BlockLocation fromValues(World world, double x, double y, double z) {
            return new BlockLocation(new Location(world, x, y, z));
        }

        static Location toLocation(BlockLocation blockLocation) {
            return blockLocation.location.clone();
        }

        static Set<Location> toLocations(Set<BlockLocation> blockLocations) {
            Set<Location> locations = new HashSet<>();
            for (BlockLocation bl : blockLocations) {
                locations.add(toLocation(bl));
            }
            return locations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BlockLocation)) return false;
            Location other = ((BlockLocation) o).location;

            return (this.location.getX() == other.getX()) && (this.location.getY() == other.getY()) && (this.location.getZ() == other.getZ()) && (this.location.getWorld().equals(other.getWorld()));
        }

        private int calculateHashCode() {
            int result = 17;
            result = 31 * result + location.getBlockX();
            result = 31 * result + location.getBlockY();
            result = 31 * result + location.getBlockZ();
            return  31 * result + location.getWorld().getUID().hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
