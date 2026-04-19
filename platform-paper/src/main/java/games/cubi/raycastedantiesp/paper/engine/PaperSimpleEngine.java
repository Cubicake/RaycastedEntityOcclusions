package games.cubi.raycastedantiesp.paper.engine;

import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.core.engine.Engine;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.players.PlayerRegistry;
import games.cubi.raycastedantiesp.core.view.BlockView;
import games.cubi.raycastedantiesp.core.view.EntityView;
import games.cubi.raycastedantiesp.paper.PaperParticleSpawner;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import games.cubi.raycastedantiesp.paper.worldguard.RegionActivationService;
import games.cubi.locatables.BlockLocatable;
import games.cubi.locatables.Locatable;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;
import java.util.function.IntSupplier;

public class PaperSimpleEngine implements Engine {
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;
    private final RaycastedAntiESP plugin;
    private final IntSupplier currentTickSupplier;
    //private final BukkitESM entitySnapshotManager;
    private final games.cubi.raycastedantiesp.core.engine.SimpleEngine delegate;

    public PaperSimpleEngine(RaycastedAntiESP plugin, ConfigManager cfg, IntSupplier currentTickSupplier) {
        this.plugin = plugin;
        this.currentTickSupplier = currentTickSupplier;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        bukkitScheduler = plugin.getServer().getScheduler();
        delegate = new games.cubi.raycastedantiesp.core.engine.SimpleEngine(
                cfg,
                new PaperParticleSpawner(),
                PlayerRegistry.getInstance()::getAllPlayerData,
                currentTickSupplier,
                this::isViewerCullingEnabled,
                this::shouldCullTarget,
                playerData -> restoreViewerVisibility(playerData, currentTickSupplier.getAsInt())
        );
        RaycastedAntiESP.getRegionActivationService().addListener(new RegionActivationService.Listener() {
            @Override
            public void onPlayerEnteredEnabledRegion(UUID playerUUID) {
            }

            @Override
            public void onPlayerExitedEnabledRegion(UUID playerUUID) {
                restoreViewerVisibility(playerUUID);
            }
        });

        //forceEntityLocationUpdate();
    }

    @Override
    public void tick() {
        delegate.tick();
    }

    private boolean isViewerCullingEnabled(PlayerData playerData) {
        if (playerData == null || playerData.hasBypassPermission()) {
            return false;
        }
        Locatable ownLocation = playerData.ownLocation();
        if (ownLocation == null || ownLocation.world() == null) {
            return false;
        }
        return RaycastedAntiESP.getRegionActivationService().isEnabled(ownLocation);
    }

    private boolean shouldCullTarget(PlayerData playerData, Locatable targetLocation) {
        if (targetLocation == null || targetLocation.world() == null) {
            return false;
        }
        Locatable ownLocation = playerData.ownLocation();
        if (ownLocation == null || ownLocation.world() == null) {
            return false;
        }
        return RaycastedAntiESP.getRegionActivationService().isEnabled(ownLocation, targetLocation);
    }

    private void restoreViewerVisibility(UUID playerUUID) {
        PlayerData playerData = PlayerRegistry.getInstance().getPlayerData(playerUUID);
        if (playerData == null) {
            return;
        }
        restoreViewerVisibility(playerData, currentTickSupplier.getAsInt());
    }

    private void restoreViewerVisibility(PlayerData playerData, int currentTick) {
        restoreEntityView(playerData.entityView(), currentTick);
        restoreEntityView(playerData.playerView(), currentTick);
        restoreBlockView(playerData.blockView(), currentTick);
    }

    private void restoreEntityView(EntityView<?> entityView, int currentTick) {
        for (UUID entityUUID : entityView.getKnownEntities()) {
            entityView.setVisibility(entityUUID, true, currentTick);
        }
    }

    private void restoreBlockView(BlockView blockView, int currentTick) {
        for (BlockLocatable location : blockView.getKnownTileEntities()) {
            blockView.setVisibility(location, true, currentTick);
        }
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
