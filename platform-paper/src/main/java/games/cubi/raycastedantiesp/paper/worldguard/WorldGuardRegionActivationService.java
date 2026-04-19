package games.cubi.raycastedantiesp.paper.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import games.cubi.locatables.Locatable;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.paper.locatables.LocatableAdapterUtils;
import games.cubi.raycastedantiesp.paper.utils.PaperListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public final class WorldGuardRegionActivationService implements RegionActivationService {
    public static final String ENABLED_FLAG_NAME = "raycasted-antiesp-enabled";
    public static final String DISABLED_FLAG_NAME = "raycasted-antiesp-disabled";

    private static volatile StateFlag enabledFlag;
    private static volatile StateFlag disabledFlag;

    private final BooleanSupplier defaultEnabledSupplier;
    private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<UUID, Boolean> playerStates = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Object queryLock = new Object();

    private volatile PlayerStateListener playerStateListener;

    private WorldGuardRegionActivationService(BooleanSupplier defaultEnabledSupplier) {
        this.defaultEnabledSupplier = Objects.requireNonNull(defaultEnabledSupplier, "defaultEnabledSupplier");
    }

    public static RegionActivationService create(BooleanSupplier defaultEnabledSupplier) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return new NoOpRegionActivationService(defaultEnabledSupplier);
        }
        if (enabledFlag == null || disabledFlag == null) {
            Logger.warning("WorldGuard is present, but activation flags were not registered. Falling back to default activation behaviour.", 2, WorldGuardRegionActivationService.class);
            return new NoOpRegionActivationService(defaultEnabledSupplier);
        }

        return new WorldGuardRegionActivationService(defaultEnabledSupplier);
    }

    public static boolean registerFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag registeredEnabledFlag = resolveOrRegisterFlag(registry, ENABLED_FLAG_NAME);
        StateFlag registeredDisabledFlag = resolveOrRegisterFlag(registry, DISABLED_FLAG_NAME);

        if (registeredEnabledFlag == null || registeredDisabledFlag == null) {
            return false;
        }

        enabledFlag = registeredEnabledFlag;
        disabledFlag = registeredDisabledFlag;
        return true;
    }

    private static StateFlag resolveOrRegisterFlag(FlagRegistry registry, String flagName) {
        try {
            StateFlag flag = new StateFlag(flagName, false);
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get(flagName);
            if (existing instanceof StateFlag stateFlag) {
                Logger.warning("WorldGuard flag '" + flagName + "' is already registered; reusing it.", 4, WorldGuardRegionActivationService.class);
                return stateFlag;
            }

            Logger.error(new IllegalStateException("Conflicting WorldGuard flag '" + flagName + "' exists with a different type.", e), 1, WorldGuardRegionActivationService.class);
            return null;
        }
    }

    @Override
    public boolean isEnabled(Locatable locatable) {
        if (locatable == null || locatable.world() == null) {
            return false;
        }

        Location location = LocatableAdapterUtils.toBukkitLocation(locatable);
        if (location.getWorld() == null) {
            return false;
        }

        return computeEnabled(location);
    }

    @Override
    public boolean isEnabled(Locatable first, Locatable second) {
        if (first == null || second == null) {
            return false;
        }
        synchronized (queryLock) {
            return isEnabled(first) && isEnabled(second);
        }
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        playerStateListener = new PlayerStateListener(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayerState(player.getUniqueId(), player.getLocation());
        }
    }

    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }

        PlayerStateListener existingListener = playerStateListener;
        if (existingListener != null) {
            HandlerList.unregisterAll(existingListener);
            playerStateListener = null;
        }

        listeners.clear();
        playerStates.clear();
    }

    private boolean computeEnabled(Location location) {
        synchronized (queryLock) {
            ApplicableRegionSet regions = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .createQuery()
                    .getApplicableRegions(BukkitAdapter.adapt(location));

            if (regions.testState(null, disabledFlag)) {
                return false;
            }
            if (regions.testState(null, enabledFlag)) {
                return true;
            }

            return defaultEnabledSupplier.getAsBoolean();
        }
    }

    private void refreshPlayerState(UUID playerUUID, Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }

        boolean enabled = computeEnabled(location);
        Boolean previous = playerStates.put(playerUUID, enabled);
        if (previous == null) {
            if (enabled) {
                notifyEnter(playerUUID);
            }
            return;
        }
        if (previous == enabled) {
            return;
        }

        if (enabled) {
            notifyEnter(playerUUID);
        } else {
            notifyExit(playerUUID);
        }
    }

    private void removePlayerState(UUID playerUUID) {
        playerStates.remove(playerUUID);
    }

    private void notifyEnter(UUID playerUUID) {
        for (Listener listener : listeners) {
            listener.onPlayerEnteredEnabledRegion(playerUUID);
        }
    }

    private void notifyExit(UUID playerUUID) {
        for (Listener listener : listeners) {
            listener.onPlayerExitedEnabledRegion(playerUUID);
        }
    }

    private static final class PlayerStateListener extends PaperListener {
        private final WorldGuardRegionActivationService service;

        private PlayerStateListener(WorldGuardRegionActivationService service) {
            this.service = service;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
            service.refreshPlayerState(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            service.removePlayerState(event.getPlayer().getUniqueId());
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerMove(PlayerMoveEvent event) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null) {
                return;
            }
            if (from.getWorld() != null && from.getWorld().equals(to.getWorld())
                    && from.getBlockX() == to.getBlockX()
                    && from.getBlockY() == to.getBlockY()
                    && from.getBlockZ() == to.getBlockZ()) {
                return;
            }

            service.refreshPlayerState(event.getPlayer().getUniqueId(), to);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            service.refreshPlayerState(event.getPlayer().getUniqueId(), event.getTo());
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
            service.refreshPlayerState(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            service.refreshPlayerState(event.getPlayer().getUniqueId(), event.getRespawnLocation());
        }
    }
}
