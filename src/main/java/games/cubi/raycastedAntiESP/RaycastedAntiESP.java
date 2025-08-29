package games.cubi.raycastedAntiESP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import games.cubi.raycastedAntiESP.engine.Engine;
import games.cubi.raycastedAntiESP.packets.PacketProcessor;
import games.cubi.raycastedAntiESP.packets.Registrar;
import games.cubi.raycastedAntiESP.raycast.MovementTracker;
import games.cubi.raycastedAntiESP.snapshot.ChunkSnapshotManager;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.utils.DataHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import games.cubi.raycastedAntiESP.bStats.MetricsCollector;

public class RaycastedAntiESP extends JavaPlugin implements CommandExecutor {
    private static ConfigManager cfg;
    private static ChunkSnapshotManager snapMgr;
    private static MovementTracker tracker;
    private static CommandsManager commands;
    private static Engine engine;
    private boolean packetEventsPresent = false; // Don't use this to check if PacketEvents is present, use DataHolder's packetevents field instead. This just checks  if its present, not if its enabled/functional
    private static PacketProcessor packetProcessor = null;
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
        cfg = ConfigManager.initiateConfigManager(this);
        snapMgr = new ChunkSnapshotManager(this, cfg);
        tracker = new MovementTracker(this, cfg);
        commands = new CommandsManager(this, cfg);
        //engine = new Engine(this, cfg);
        //new UpdateChecker(this);
        UpdateChecker.checkForUpdates(this, Bukkit.getConsoleSender());
        getServer().getPluginManager().registerEvents(new EventListener(this, snapMgr, cfg, engine), this);
        //Brigadier API
        LiteralCommandNode<CommandSourceStack> buildCommand = commands.registerCommand();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(buildCommand);
            //alias "reo"
            commands.registrar().register(Commands.literal("reo")
                    .requires(sender -> sender.getSender().hasPermission("raycastedentityocclusions.command"))
                    .executes(context -> {
                        new CommandsManager(this, cfg).helpCommand(context);
                        return Command.SINGLE_SUCCESS;
                    })
                    .redirect(buildCommand).build());
        });

        //bStats
        new MetricsCollector(this, cfg);


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
                    DataHolder.packetEventsPresent = true;
                    packetProcessor = new PacketProcessor(RaycastedAntiESP.this);
                    Logger.info("PacketEvents is enabled, enabling packet-based tablist modification.");
                }
            }
        }.runTaskLater(this, 1L);
    }


    public static ConfigManager getConfigManager() {
        return cfg;
    }
    public static ChunkSnapshotManager getChunkSnapshotManager() {
        return snapMgr;
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