package games.cubi.raycastedAntiESP.engine;

import games.cubi.raycastedAntiESP.config.*;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.utils.DataHolder;
import games.cubi.raycastedAntiESP.utils.ThreadSafeLoc;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EngineNewer {
    private final AsyncScheduler asyncScheduler;

    private final RaycastedAntiESP plugin;
    private final ConfigManager config;

    public EngineNewer(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
    }

    //Todo: call this method from EventListener onTickStart
    public void gatherData() {
        if (true /* Todo: replace this with a check for entitysnapshotinterval or something*/) {}
    }

    private void forceEntityLocationUpdate() {
        HashMap<UUID, ThreadSafeLoc> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) continue; // Skip players, they are handled separately
                entities.put(entity.getUniqueId(), new ThreadSafeLoc(entity.getLocation(), entity.getHeight()));
            }

        }
        DataHolder.updateEntireEntityLocationMap(entities);
    }
}
