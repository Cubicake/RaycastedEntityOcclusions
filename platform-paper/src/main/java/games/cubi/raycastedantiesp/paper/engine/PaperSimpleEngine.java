package games.cubi.raycastedantiesp.paper.engine;

import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.core.engine.Engine;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.paper.PaperParticleSpawner;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import games.cubi.raycastedantiesp.paper.data.DataHolder;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.scheduler.BukkitScheduler;

public class PaperSimpleEngine implements Engine {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;
    private final RaycastedAntiESP plugin;
    //private final BukkitESM entitySnapshotManager;
    private final games.cubi.raycastedantiesp.core.engine.SimpleEngine delegate;

    public PaperSimpleEngine(RaycastedAntiESP plugin, ConfigManager cfg) {
        this.plugin = plugin;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();
        delegate = new games.cubi.raycastedantiesp.core.engine.SimpleEngine(cfg, new PaperParticleSpawner(), PlayerRegistry.getInstance()::getAllPlayerData, DataHolder::getTick);

        //forceEntityLocationUpdate();
    }

    @Override
    public void tick() {
        delegate.tick();
    }
/*
    private void forceEntityLocationUpdate() {  //todo: Quite frankly idk if this is needed, disabled for now since the config option doesn't even exist
        int recheckInterval = -1;//ConfigManager.get().getSnapshotConfig().getEntityLocationRefreshInterval();
        if (recheckInterval <= 0) {
            //bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, 20 * 30); // Check again in 30 secs if config has changed
            return;
        }

        HashMap<UUID, ThreadSafeLocatable> entities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                entities.put(entity.getUniqueId(), LocatableAdapterUtils.toLocatable(entity.getLocation(), entity.getHeight(), ThreadSafeLocatable.class));
            }
        }
        entitySnapshotManager.updateEntireEntityLocationMap(entities);
        bukkitScheduler.runTaskLater(plugin, this::forceEntityLocationUpdate, recheckInterval);
    }*/
}
