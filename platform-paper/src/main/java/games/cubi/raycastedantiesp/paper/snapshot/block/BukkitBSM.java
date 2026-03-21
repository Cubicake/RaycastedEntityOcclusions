package games.cubi.raycastedantiesp.paper.snapshot.block;

import games.cubi.logs.Frequency;
import games.cubi.raycastedantiesp.paper.EventListener;
import games.cubi.raycastedantiesp.paper.config.ConfigManager;
import games.cubi.raycastedantiesp.paper.Logger;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.implementations.ImmutableBlockLocatable;

import games.cubi.raycastedantiesp.paper.locatables.LocatableAdapterUtils;
import games.cubi.raycastedantiesp.paper.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.paper.utils.PlayerData;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BukkitBSM implements BlockSnapshotManager {
    private static class ChunkData {
        public final ChunkSnapshot snapshot;
        public final ConcurrentHashMap<ImmutableBlockLocatable, Material> delta = new ConcurrentHashMap<>();
        public final Set<ImmutableBlockLocatable> tileEntities = ConcurrentHashMap.newKeySet();
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

    public BukkitBSM(RaycastedAntiESP plugin, ConfigManager config) {
        cfg = config;
        this.plugin = plugin;
        //get loaded chunks and add them to dataMap
        for (World w : plugin.getServer().getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                snapshotChunk(c);
            }
        }

        int refreshInterval = 60*2;

        if (cfg.getSnapshotConfig().getBukkitBlockSnapshotConfig() != null) {
            refreshInterval = cfg.getSnapshotConfig().getBukkitBlockSnapshotConfig().getRefreshRateSeconds() * 2;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                int chunksRefreshed = 0;
                int chunksToRefreshMaximum = getNumberOfCachedChunks() / 3;
                for (Map.Entry<String, ChunkData> e : dataMap.entrySet()) {
                    if (cfg.getSnapshotConfig().getBukkitBlockSnapshotConfig() == null) {
                        return;
                    }
                    if (now - e.getValue().lastRefresh >= cfg.getSnapshotConfig().getBukkitBlockSnapshotConfig().getRefreshRateSeconds() * 1000L && chunksRefreshed < chunksToRefreshMaximum) {
                        chunksRefreshed++;
                        String key = e.getKey();
                        snapshotChunk(key);
                    }
                }
                Logger.info("BukkitBSM: Refreshed " + chunksRefreshed + " chunks out of " + chunksToRefreshMaximum + " maximum.", Frequency.ONCE_PER_TICK.value);
            }
        }.runTaskTimerAsynchronously(plugin, refreshInterval, refreshInterval /* This runs 10 times per getRefreshRateSeconds, spreading out the refreshes */);
    }

    public void onChunkLoad(Chunk c) {
        snapshotChunk(c);
    }

    public void onChunkUnload(Chunk c) {
        removeChunkSnapshot(c);
    }

    public void snapshotChunk(Chunk c) {
        Logger.info("BukkitBSM: Taking snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ(), Frequency.MULTI_PER_TICK.value);
        dataMap.put(key(c), takeSnapshot(c, System.currentTimeMillis()));
    }
    public void snapshotChunk(String key) {
        snapshotChunk(getKeyChunk(key));
    }
    public void removeChunkSnapshot(Chunk c) {
        Logger.info("BukkitBSM: Removing snapshot of chunk " + c.getWorld().getName() + ":" + c.getX() + ":" + c.getZ(), 9);
        dataMap.remove(key(c));
    }

    // Used by EventListener to update the delta map when a block is placed or broken
    public void onBlockChange(Location loc, Material m, int change) {
        Logger.info("BukkitBSM: Block change at " + loc + " to " + m, Frequency.MULTI_PER_TICK.value);
        ChunkData d = dataMap.get(key(loc.getChunk()));
        if (d == null) {
            Logger.error("Data map value empty, ignoring block update!",3);
        }
        ImmutableBlockLocatable blockLoc = LocatableAdapterUtils.toLocatable(loc, ImmutableBlockLocatable.class);

        d.delta.put(blockLoc, m);
        if (cfg.getTileEntityConfig().isEnabled()) {
            // Check if the block is a tile entity
            BlockState data = loc.getBlock().getState();
            if (data instanceof TileState) {
                Logger.info("BukkitBSM: Tile entity at " + loc, 8);
                if (change == EventListener.PLACE) {
                    d.tileEntities.add(blockLoc);
                }
                if (change == EventListener.BREAK) {
                    d.tileEntities.remove(blockLoc);
                }
            }
        }
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
                            data.tileEntities.add(new ImmutableBlockLocatable(w.getUID(), x+ chunkX +0.5, y+0.5, z + chunkZ+0.5));
                        }
                    }
                }
            }
        }
        return data;
    }

    public String key(Chunk c) {
        return c.getWorld().getUID() + ":" + c.getX() + ":" + c.getZ();
    }
    public String key(World world, int x, int z) {
        return world.getUID() + ":" + x + ":" + z;
    }
    public String key(BlockLocatable location) {
        //make a string builder, UUID is location.world()
        StringBuilder sb = new StringBuilder();
        sb.append(location.world().toString());
        sb.append(":");
        sb.append(location.blockX() >> 4);
        sb.append(":");
        sb.append(location.blockZ() >> 4);
        return sb.toString();
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
        return Bukkit.getWorld(java.util.UUID.fromString(parts[0]));
    }
    public Chunk getKeyChunk(String key) {
        return getKeyWorld(key).getChunkAt(getKeyX(key), getKeyZ(key));
    }

    static final AtomicInteger stupidErrorCounter = new AtomicInteger(0);

    public Material getMaterialAt(BlockLocatable loc) {
        ChunkData chunkData = dataMap.get(key(loc));
        if (chunkData == null) {
            if (stupidErrorCounter.addAndGet(1) % 500 == 0) {
                Logger.error("BukkitBSM: No snapshot for " + loc + " If this error persists, please report this on our discord (discord.cubi.games)", 5);
            }
            RaycastedAntiESP.getEngine().recheckQueue.add(loc);
            return null;
        }
        double yLevel = loc.y();
        if (yLevel < chunkData.minHeight || yLevel > chunkData.maxHeight) {
            return null;
        }
        Material dm = chunkData.delta.get(loc);
        if (dm != null) {
            Logger.info("Using delta", 9);
            return dm;
        }
        int x = loc.blockX() & 0xF;
        int y = loc.blockY();
        int z = loc.blockZ() & 0xF;

        return chunkData.snapshot.getBlockType(x, y, z);
    }

    @Override
    public boolean isBlockOccluding(BlockLocatable locatable, PlayerData player) {
        Material m = getMaterialAt(locatable);
        if (m == null) {
            return false;
        }
        return m.isOccluding();
    }

    public SnapshotManager.BlockSnapshotManagerType getType() {
        return SnapshotManager.BlockSnapshotManagerType.BUKKIT;
    }

    //get TileEntity Locations in chunk
    public Set<ImmutableBlockLocatable> getTileEntitiesInChunk(World world, int x, int z) {
        ChunkData d = dataMap.get(key(world, x, z));
        if (d == null) {
            return Collections.emptySet();
        }
        return d.tileEntities; //TODO: this is returning the wrong type
    }

    public void removeTileEntity(Location loc) {
        Chunk c = loc.getChunk();
        ChunkData d = dataMap.get(key(c));
        if (d != null) {
            d.tileEntities.remove(LocatableAdapterUtils.toLocatable(loc, ImmutableBlockLocatable.class));
            Logger.info("ChunkSnapshotManager: Removed tile entity at " + loc,9);
        } else {
            Logger.error("ChunkSnapshotManager: No snapshot for " + c + " when removing tile entity at " + loc,9);
        }
    }

    public int getNumberOfCachedChunks() {
        return dataMap.size();
        //created to use in a info command maybe
    }

    @Override
    public Set<ImmutableBlockLocatable> getTileEntitiesInChunk(UUID world, int x, int z, PlayerData player) {
        return getTileEntitiesInChunk(Bukkit.getWorld(world), x, z);
    }
}
