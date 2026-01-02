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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import games.cubi.raycastedAntiESP.bStats.MetricsCollector;

public final class RaycastedAntiESP extends JavaPlugin implements CommandExecutor {
    private static ConfigManager cfg;
    private static MovementTracker tracker;
    private static CommandsManager commands;
    private boolean packetEventsPresent = false; // Don't use this to check if PacketEvents is present, use DataHolder's packetevents field instead. This just checks  if its present, not if its enabled/functional
    private static PacketProcessor packetProcessor = null;
    private static Engine engine;
    private static MetricsCollector metricsCollector;
    private static RaycastedAntiESP instance;
    private static java.util.logging.Logger logger;

    public int tick = 0;

    @Override
    public void onLoad() {
        logger = getLogger();
        Plugin packetEvents = Bukkit.getPluginManager().getPlugin("packetevents");
        if (packetEvents != null) {
            packetEventsPresent = true;
            getLogger().info("PacketEvents detected.");
            new Registrar(this);

        } else {
            getLogger().info("PacketEvents not detected, disabling packet-based tablist modification. Don't worry, the plugin will still work without it.");
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        cfg = ConfigManager.initialiseConfigManager(this);
        tracker = new MovementTracker(this, cfg);
        commands = new CommandsManager(this, cfg);
        engine = new Engine(this, cfg);
        UpdateChecker.checkForUpdates(this, Bukkit.getConsoleSender());
        getServer().getPluginManager().registerEvents(EventListener.getInstance(this, cfg), this);
        //Brigadier API
        LiteralCommandNode<CommandSourceStack> buildCommand = commands.registerCommand();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(buildCommand);
            //alias "antiesp"
            commands.registrar().register(Commands.literal("antiesp")
                    .requires(sender -> sender.getSender().hasPermission("raycastedantiesp.command"))
                    .executes(context -> {
                        new CommandsManager(this, cfg).helpCommand(context);
                        return Command.SINGLE_SUCCESS;
                    })
                    .redirect(buildCommand).build());
        });

        SnapshotManager.initialise(new BukkitBSM(this, cfg), new BukkitESM(), new BukkitTSM());
        VisibilityChangeHandlers.initialise(new BukkitEVC(), new BukkitPVC(), new BukkitTVC());

        //bStats
        metricsCollector =  new MetricsCollector(this, cfg);


        // TODO: Move this somewhere else, the main class should be cleaner
        new BukkitRunnable() {
            @Override
            public void run() {
                tick++; /*
                EngineOld.runEngine(cfg, snapMgr, tracker, RaycastedEntityOcclusion.this);
                EngineOld.runTileEngine(cfg, snapMgr, tracker, RaycastedEntityOcclusion.this);*/
            }
        }.runTaskTimer(this, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (packetEventsPresent && Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
                    PacketEventsStatus.init(true);
                    packetProcessor = new PacketProcessor(RaycastedAntiESP.get());
                    Logger.info("PacketEvents is enabled, enabling packet-based tablist modification.", 3);
                }
                else PacketEventsStatus.init(false);
            }
        }.runTaskLater(this, 1L);
    }

    @Override
    public void onDisable() {
        Logger.flush();
        metricsCollector.shutdown();
    }


    public static ConfigManager getConfigManager() {
        return cfg;
    }
    public static MovementTracker getMovementTracker() {
        return tracker;
    }
    public static CommandsManager getCommandsManager() {
        return commands;
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