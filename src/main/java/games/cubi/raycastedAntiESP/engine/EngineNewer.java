package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.utils.DataHolder;
import games.cubi.raycastedAntiESP.utils.ThreadSafeLoc;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    }

    //Todo: call this method from EventListener onTickStart
    public void gatherData() {
        if (true /* Todo: replace this with a check for entitysnapshotinterval or something*/) {}
    }

    private void forceEntityLocationUpdate() {
        int recheckInterval = ConfigManager.get().getSnapshotConfig().getEntityLocationRefreshInterval();
        if (recheckInterval <= 0) {
            bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, 20 * 30); // Check again in 30 secs if config has changed
            return;
        };
        HashMap<UUID, ThreadSafeLoc> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entities.put(entity.getUniqueId(), new ThreadSafeLoc(entity.getLocation(), entity.getHeight()));
            }
        }
        DataHolder.updateEntireEntityLocationMap(entities);
        bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
    }

    private void processEntityMovements(ScheduledTask scheduledTask) {
        DataHolder.processEntityLocationQueue();
        asyncScheduler.runDelayed(plugin, this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }

    private void clearOldCacheEntries() {
        DataHolder.cleanShouldShowEntityCache();
        bukkitScheduler.runTaskLater(plugin, this::clearOldCacheEntries, 20 * 120);
    }

}
