package games.cubi.raycastedAntiESP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import games.cubi.raycastedAntiESP.engine.Engine;
import games.cubi.raycastedAntiESP.packets.PacketEventsStatus;
import games.cubi.raycastedAntiESP.packets.PacketProcessor;
import games.cubi.raycastedAntiESP.packets.Registrar;
import games.cubi.raycastedAntiESP.raycast.MovementTracker;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.block.BukkitBSM;
import games.cubi.raycastedAntiESP.snapshot.entity.BukkitESM;
import games.cubi.raycastedAntiESP.snapshot.tileentity.BukkitTSM;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.entity.BukkitEVC;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.player.BukkitPVC;
import games.cubi.raycastedAntiESP.visibilitychangehandlers.tileentity.BukkitTVC;
import games.cubi.raycastedAntiESP.bStats.MetricsCollector;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaycastedAntiESP extends JavaPlugin implements CommandExecutor {
    private static ConfigManager config;
    private static MovementTracker tracker;
    private boolean packetEventsPresent = false; // Don't use this to check if PacketEvents is present, use DataHolder's packetevents field instead. This just checks  if its present, not if its enabled/functional
    private static PacketProcessor packetProcessor = null;
    private static Engine engine;
    private static MetricsCollector metricsCollector;
    private static RaycastedAntiESP instance;
    private static java.util.logging.Logger logger;

    @Override
    public void onLoad() {
        logger = getLogger();
        Plugin packetEvents = Bukkit.getPluginManager().getPlugin("packetevents");
        if (packetEvents == null) {
            Logger.info("PacketEvents not detected, disabling packet-based tablist modification. Don't worry, the plugin will still work without it.", 4);
        }
        else {
            packetEventsPresent = true;
            getLogger().info("PacketEvents detected.");
            new Registrar(this);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        config = ConfigManager.initialiseConfigManager(this);
        tracker = new MovementTracker(this, config);
        engine = new Engine(this, config);
        UpdateChecker.checkForUpdates(this, Bukkit.getConsoleSender());
        getServer().getPluginManager().registerEvents(EventListener.getInstance(this, config), this);

        initialiseCommands();

        SnapshotManager.initialise(new BukkitBSM(this, config), new BukkitESM(), new BukkitTSM());
        VisibilityChangeHandlers.initialise(new BukkitEVC(), new BukkitPVC(), new BukkitTVC());

        //bStats
        metricsCollector =  new MetricsCollector(this, config);

        Bukkit.getGlobalRegionScheduler().runDelayed(this, (task) -> {initialisePacketProcessor(); task.cancel();}, 1);
    }

    @Override
    public void onDisable() {
        Logger.flush();
        metricsCollector.shutdown();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initialiseCommands() {
        LiteralCommandNode<CommandSourceStack> buildCommand = CommandsManager.registerCommand(this, config);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(buildCommand);
            //alias "antiesp"
            commands.registrar().register(Commands.literal("antiesp")
                    .requires(sender -> sender.getSender().hasPermission("raycastedantiesp.command"))
                    .executes(context -> {
                        CommandsManager.helpCommand(context, this);
                        return Command.SINGLE_SUCCESS;
                    })
                    .redirect(buildCommand).build());
        });
    }

    private void initialisePacketProcessor() {
        if (packetEventsPresent && Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            PacketEventsStatus.init(true);
            packetProcessor = new PacketProcessor(RaycastedAntiESP.get());
            Logger.info("PacketEvents is enabled, enabling packet-based tablist modification.", 3);
        }
        else PacketEventsStatus.init(false);
    }


    public static ConfigManager getConfigManager() {
        return config;
    }
    public static MovementTracker getMovementTracker() {
        return tracker;
    }
    public static PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }
    public static Engine getEngine() {
        return engine;
    }
    public static RaycastedAntiESP get() {
        return instance;
    }
    public static java.util.logging.Logger logger() {
        return logger;
    }
}