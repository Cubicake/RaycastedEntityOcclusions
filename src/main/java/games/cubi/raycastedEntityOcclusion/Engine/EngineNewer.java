package games.cubi.raycastedEntityOcclusion.Engine;

import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;
import games.cubi.raycastedEntityOcclusion.Utils.DataHolder;
import games.cubi.raycastedEntityOcclusion.Utils.QuantisedLocation;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EngineNewer {
    private final AsyncScheduler asyncScheduler;

    private final RaycastedEntityOcclusion plugin;
    private final ConfigManager config;

    public EngineNewer(RaycastedEntityOcclusion plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
    }

    //Todo: call this method from EventListener onTickStart
    public void gatherData() {
        if (true /* Todo: replace this with a check for entitysnapshotinterval or something*/)
    }

    private void forceEntityLocationUpdate() {
        HashMap<UUID, QuantisedLocation> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) continue; // Skip players, they are handled separately
                entities.put(entity.getUniqueId(), new QuantisedLocation(entity.getLocation(), entity.getHeight()));
            }

        }
        DataHolder.updateEntireEntityLocationMap(entities);
    }
}
