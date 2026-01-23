package games.cubi.raycastedAntiESP.deletioncandidates;
@Deprecated(forRemoval = true)
public class EngineOld {}
/*
import games.cubi.raycastedEntityOcclusion.Logger;
import games.cubi.raycastedEntityOcclusion.Snapshot.ChunkSnapshotManager;
import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Color;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EngineOld {

    public static ConcurrentHashMap<Location, Set<Player>> canSeeTileEntity = new ConcurrentHashMap<>();
    public static Set<Chunk> syncRecheck = ConcurrentHashMap.newKeySet();

    private final static BlockData STONE = Material.STONE.createBlockData();
    private final static BlockData DEEPSLATE = Material.DEEPSLATE.createBlockData();

    private static class RayJob {
        final Player player;
        final Entity target;
        final Location start, predictedStart, end;

        RayJob(Player p, Entity e, Location s, Location pred, Location t) {
            player = p;
            target = e;
            start = s;
            predictedStart = pred;
            end = t;
        }
    }

    private static class RayResult {
        final Player player;
        final Entity target;
        final boolean visible;

        RayResult(Player p, Entity e, boolean v) {
            player = p;
            target = e;
            visible = v;
        }
    }
    private static class TileResult {
        final Player player;
        final Location loc;
        final boolean visible;

        TileResult(Player p, Location location, boolean v) {
            player = p;
            loc = location;
            visible = v;
        }
    }
        TileResult(Player p, Location location, boolean v) {
            player = p;
            loc = location;
            visible = v;
        }
    }

    public static void runEngine(ConfigManager cfg, ChunkSnapshotManager snapMgr, MovementTracker tracker, RaycastedAntiESP plugin) {
        // ----- PHASE 1: SYNC GATHER -----

        if (!syncRecheck.isEmpty()) {
            Logger.warning(syncRecheck.size() + " chunks failed to snapshot asynchronously, rechecking them now.");
            for (Chunk c : syncRecheck) {
                if (c.isLoaded()) {
                    plugin.getChunkSnapshotManager().snapshotChunk(c);
                    syncRecheck.remove(c);
                    Logger.info("Successfully rechecked chunk " + c.getX() + ", " + c.getZ() + " in world " + c.getWorld().getName());
                }
            }
        }

        List<RayJob> jobs = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("raycastedentityocclusions.bypass")) continue;
            Location eye = p.getEyeLocation().clone();
            Location predEye = null;
            if (cfg.engineMode == 2) {
                // getPredictedLocation returns null if insufficient data or too slow
                predEye = tracker.getPredictedLocation(p);
            }

            for (Entity e : p.getNearbyEntities(cfg.searchRadius, cfg.searchRadius, cfg.searchRadius)) {
                if (e == p) continue;
                // Cull-players logic
                if (e instanceof Player pl && (!cfg.cullPlayers || (cfg.onlyCullSneakingPlayers && !pl.isSneaking()))) {
                    p.showEntity(plugin, e);
                    continue;
                }

                Location target = e.getLocation().add(0, e.getHeight() / 2, 0).clone();
                double dist = eye.distance(target);
                if (dist <= cfg.alwaysShowRadius) {
                    p.showEntity(plugin, e);
                } else if (dist > cfg.raycastRadius) {
                    p.hideEntity(plugin, e);
                } else if (!p.canSee(e) || plugin.tick % cfg.recheckInterval == 0) {
                    // schedule for async raycast (with or without predEye)
                    jobs.add(new RayJob(p, e, eye, predEye, target));
                }
            }
        }

        // ----- PHASE 2: ASYNC RAYCASTS -----
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<RayResult> results = new ArrayList<>(jobs.size());
            for (RayJob job : jobs) {
                // if the player is not in the same world as the target, skip
                if (!job.player.getWorld().equals(job.target.getWorld())) {
                    continue;
                }
                // first cast from real eye
                boolean vis = RaycastUtil.raycast(job.start, job.end, cfg.maxOccludingCount, cfg.debugMode, snapMgr);

                // if that fails and we have a predEye, cast again from predicted
                if (!vis && job.predictedStart != null) {
                    if (cfg.debugMode) {
                        job.predictedStart.getWorld().spawnParticle(Particle.DUST, job.predictedStart, 1, new Particle.DustOptions(Color.BLUE, 1f));
                    }
                    vis = RaycastUtil.raycast(job.predictedStart, job.end, cfg.maxOccludingCount, cfg.debugMode, snapMgr);
                }

                results.add(new RayResult(job.player, job.target, vis));
            }

            // ----- PHASE 3: SYNC APPLY -----
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (RayResult r : results) {
                    Player p = r.player;
                    Entity ent = r.target;
                    if (p != null && ent != null) {
                        boolean currentState = p.canSee(ent);
                        if (currentState == r.visible) continue;
                        if (r.visible) p.showEntity(plugin, ent);
                        else {
                            p.hideEntity(plugin, ent);
                        }
                    }
                }
            });
        });

    }

    private final static ConcurrentLinkedQueue<TileResult> results = new ConcurrentLinkedQueue<>();

    public static void runTileEngine(ConfigManager cfg, ChunkSnapshotManager snapMgr, MovementTracker tracker, RaycastedAntiESP plugin) {
    private static ConcurrentLinkedQueue<TileResult> results = new ConcurrentLinkedQueue<>();

    public static void runTileEngine(ConfigManager cfg, ChunkSnapshotManager snapMgr, MovementTracker tracker, RaycastedEntityOcclusion plugin) {
        if (cfg.checkTileEntities) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("raycastedentityocclusions.bypass")) continue;
                World world = p.getWorld();
                //get player's chunk location
                int chunkX = p.getLocation().getBlockX() >> 4;
                int chunkZ = p.getLocation().getBlockZ() >> 4;
                Location playerLoc = p.getLocation();

                //async run with vars passed in
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    int chunksRadius = (cfg.searchRadius + 15) / 16;
                    HashSet<Location> tileEntities = new HashSet<>();
                    for (int x = (-chunksRadius/2)+chunkX; x <= chunksRadius+chunkX; x++) {
                        for (int z = (-chunksRadius/2)+chunkZ; z <= chunksRadius+chunkZ; z++) {
                            tileEntities.addAll(snapMgr.getTileEntitiesInChunk(world, x, z));
                        }
                    }
                    for (Location loc : tileEntities) {
                        Set<Player> seen = canSeeTileEntity.get(loc);
                        if (seen != null && seen.contains(p)) {
                            if (cfg.tileEntityRecheckInterval == 0) continue;
                            if (plugin.tick % (cfg.tileEntityRecheckInterval*20) != 0) continue;
                        }

                        if (snapMgr.getMaterialAt(loc).equals(Material.BEACON)) continue;

                        if (!loc.getWorld().equals(playerLoc.getWorld())) continue;
                        double distSquared = loc.distanceSquared(playerLoc);
                        if (distSquared > cfg.searchRadius * cfg.searchRadius) {
                            results.add(new TileResult(p, loc,false));
                            continue;
                        }
                        if (distSquared < cfg.alwaysShowRadius * cfg.alwaysShowRadius) {
                            results.add(new TileResult(p, loc,true));
                            continue;
                        }

                        boolean visible = RaycastUtil.raycast(p.getEyeLocation(), loc, cfg.maxOccludingCount, cfg.debugMode, snapMgr);
                        if (cfg.engineMode == 2 && !visible) {
                            Location predEye = tracker.getPredictedLocation(p);
                            if (predEye != null) {
                                boolean result2 = RaycastUtil.raycast(predEye, loc, cfg.maxOccludingCount, cfg.debugMode, snapMgr);
                                if (result2) {
                                    visible = true;
                                }
                            }
                        }
                        if (visible) {
                            canSeeTileEntity.computeIfAbsent(loc, k -> ConcurrentHashMap.newKeySet()).add(p);
                        } else {
                            Set<Player> seenPlayers = canSeeTileEntity.get(loc);
                            if (seenPlayers != null) {
                                seenPlayers.remove(p);
                                if (seenPlayers.isEmpty()) {
                                    canSeeTileEntity.remove(loc);
                                }
                            }
                        }
                        results.add(new TileResult(p, loc, visible));
                    }
                });
            }
            for (TileResult r : results) {
                Player p = r.player;
                Location loc = r.loc;
                boolean visible = r.visible;

                syncToggleTileEntity(p, loc, visible, plugin);
                results.remove(r);
            }
        }
    }

    public static void hideTileEntity(Player p, Location location, RaycastedAntiESP plugin) {
    public static void hideTileEntity(Player p, Location location, RaycastedEntityOcclusion plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.hasPermission("raycastedentityocclusions.bypass")) return;
            if (location.getBlockY() < 0) p.sendBlockChange(location, DEEPSLATE);
            else p.sendBlockChange(location, STONE);
        });
    }
    public static void showTileEntity(Player p, Location location, RaycastedAntiESP plugin) {
    public static void showTileEntity(Player p, Location location, RaycastedEntityOcclusion plugin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Block block = location.getBlock();
            BlockState data = block.getState();
            if (data instanceof TileState tileData) {
                p.sendBlockChange(location, block.getBlockData());
                p.sendBlockUpdate(location, tileData);
            }
            else {
                //Logger.warning("Tried to show tile entity at " + location + " to "+p.getName()+" but it was not a TileState! Block type: " + block.getType()+". Removing from the list of tile entities.");
                canSeeTileEntity.remove(location);
                plugin.getChunkSnapshotManager().removeTileEntity(location);
            }
            else throw new RuntimeException("Attempting to show a block which isn't a tile entity");
        });


    }
    public static void syncToggleTileEntity(Player p, Location loc, boolean show, RaycastedAntiESP plugin) {
    public static void syncToggleTileEntity(Player p, Location loc, boolean show, RaycastedEntityOcclusion plugin) {
        if (show) {
            showTileEntity(p, loc, plugin);
        } else {
            hideTileEntity(p, loc, plugin);
        }
    }
<<<<<<<< HEAD:src/main/java/games/cubi/raycastedAntiESP/raycast/Engine.java
}
========
}*/
