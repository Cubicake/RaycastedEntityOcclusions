package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.data.DataHolder;
import games.cubi.raycastedAntiESP.utils.PlayerData;
import games.cubi.raycastedAntiESP.utils.ThreadSafeLoc;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EngineNewer {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;

    private final RaycastedAntiESP plugin;
    private final ConfigManager config;

    public EngineNewer(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();

        forceEntityLocationUpdate();
        processEntityMovements(null); //first one will run on main thread but it shouldn't have to do much anyways
        clearOldCacheEntries();
        flushLogCache(null);
    }

    public void tick() {
        for (PlayerData playerData : DataHolder.players().getAllPlayerData()) {
            if (playerData.hasBypassPermission()) continue;
            ThreadSafeLoc playerLocation = DataHolder.entityLocation().getEntityLocation(playerData.getPlayerUUID());
            if (playerLocation == null) Logger.errorAndReturn(new RuntimeException("wtf"));
        }
    }

    private void forceEntityLocationUpdate() {
        int recheckInterval = ConfigManager.get().getSnapshotConfig().getEntityLocationRefreshInterval();
        if (recheckInterval <= 0) {
            bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, 20 * 30); // Check again in 30 secs if config has changed
            return;
        }
        HashMap<UUID, ThreadSafeLoc> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entities.put(entity.getUniqueId(), new ThreadSafeLoc(entity.getLocation(), entity.getHeight()));
            }
        }
        DataHolder.entityLocation().updateEntireEntityLocationMap(entities);
        bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
    }

    private void processEntityMovements(ScheduledTask scheduledTask) {
        DataHolder.entityLocation().processEntityLocationQueue();
        asyncScheduler.runDelayed(plugin, this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }

    private void clearOldCacheEntries() {
        DataHolder.entityVisibility().cleanShouldShowEntityCache();
        bukkitScheduler.runTaskLater(plugin, this::clearOldCacheEntries, 20 * 120);
    }

    private void flushLogCache(ScheduledTask scheduledTask) {
        if (config.getDebugConfig().logToFile()) {
            Logger.flush();
        }
        asyncScheduler.runDelayed(plugin, this::flushLogCache, 2, TimeUnit.SECONDS);
    }

}
