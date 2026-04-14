package games.cubi.raycastedantiesp.paper.snapshot.entity;

import games.cubi.locatables.implementations.ThreadSafeLocatable;
import games.cubi.raycastedantiesp.core.snapshot.PlayerEntitySnapshotManager;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import io.papermc.paper.event.entity.EntityMoveEvent;
import games.cubi.raycastedantiesp.paper.utils.EntityLocationPair;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class BukkitESM implements PlayerEntitySnapshotManager, PlayerEntitySnapshotManager.Factory, Listener {

    private final ConcurrentLinkedQueue<EntityLocationPair> entityLocProcessingQueue = new ConcurrentLinkedQueue<>();

    public BukkitESM() {
        Bukkit.getPluginManager().registerEvents(this, RaycastedAntiESP.get());
        processEntityMovements(null);
    }

    public void queueEntityLocationUpdate(UUID entityUUID, Location location, double offset) {
        entityLocProcessingQueue.add(new EntityLocationPair(entityUUID, location, offset));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        queueEntityLocationUpdate(event.getPlayer().getUniqueId(), event.getPlayer().getEyeLocation(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        queueEntityLocationUpdate(event.getPlayer().getUniqueId(), event.getPlayer().getEyeLocation(), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityMove(EntityMoveEvent event) {
        queueEntityLocationUpdate(event.getEntity().getUniqueId(), event.getTo(), event.getEntity().getHeight() / 2);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        queueEntityLocationUpdate(event.getEntity().getUniqueId(), event.getLocation(), event.getEntity().getHeight() / 2);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (var entity : event.getEntities()) {
            queueEntityLocationUpdate(entity.getUniqueId(), entity.getLocation(), entity.getHeight() / 2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (var entity : event.getEntities()) {
            removeEntityLocation(entity.getUniqueId());
        }
    }

    @Override
    public void removeEntityLocation(UUID entityUUID) {
        entityLocationMap.remove(entityUUID);
    }

    private volatile ConcurrentHashMap<UUID, ThreadSafeLocatable> entityLocationMap = new ConcurrentHashMap<>();

    public void updateEntireEntityLocationMap(HashMap<UUID, ThreadSafeLocatable> newLocations) {
        entityLocationMap = new ConcurrentHashMap<>(newLocations);
    }

    private void setOrUpdateEntityLocation(UUID entityUUID, Location location, double offset) {
        entityLocationMap.compute(entityUUID, (uuid, oldLoc) -> {
            if (oldLoc == null) return new ThreadSafeLocatable(location.getWorld().getUID(), location.x(), location.y() + offset, location.z());
            oldLoc.set(location.x(), location.y() + offset, location.z(), location.getWorld().getUID()); // Not atomic, but good enough hopefully.
            return oldLoc;
        });
    }

    // Run this async
    private void processEntityLocationQueue() {
        while (!entityLocProcessingQueue.isEmpty()) {
            EntityLocationPair entityLocationPair = entityLocProcessingQueue.poll();
            if (entityLocationPair == null) return;
            setOrUpdateEntityLocation(entityLocationPair.entity(), entityLocationPair.loc(), entityLocationPair.offset());
        }
    }

    @Override
    public ThreadSafeLocatable getLocation(UUID entityUUID) {
        return entityLocationMap.get(entityUUID);
    }

    @Override
    public SnapshotManager.SnapshotManagerType getType() {
        return SnapshotManager.SnapshotManagerType.BUKKIT;
    }

    @Override
    public PlayerEntitySnapshotManager createPlayerEntitySnapshotManager() {
        return this;
    }

    public HashMap<UUID, ThreadSafeLocatable> getCopyOfEntityLocationMap() {
        return new HashMap<>(entityLocationMap);
    }

    private void processEntityMovements(ScheduledTask scheduledTask) {
        processEntityLocationQueue();
        Bukkit.getAsyncScheduler().runDelayed(RaycastedAntiESP.get(), this::processEntityMovements, 15, TimeUnit.MILLISECONDS);
    }
}
