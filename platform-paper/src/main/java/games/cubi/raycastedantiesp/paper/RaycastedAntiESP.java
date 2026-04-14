package games.cubi.raycastedantiesp.paper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import games.cubi.raycastedantiesp.core.Core;
import games.cubi.raycastedantiesp.core.snapshot.SnapshotManager;
import games.cubi.raycastedantiesp.core.visibilitychangehandlers.VisibilityChangeHandlers;
import games.cubi.raycastedantiesp.paper.config.PaperTileEntityConfig;
import games.cubi.raycastedantiesp.paper.engine.PaperSimpleEngine;
import games.cubi.raycastedantiesp.paper.packets.PacketEventsStatus;
import games.cubi.raycastedantiesp.paper.packets.PacketProcessor;
import games.cubi.raycastedantiesp.paper.packets.Registrar;
import games.cubi.raycastedantiesp.paper.raycast.MovementTracker;
import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.paper.snapshot.block.BukkitBSM;
import games.cubi.raycastedantiesp.paper.snapshot.entity.BukkitESM;
import games.cubi.raycastedantiesp.paper.staging.PaperPESnapshotFactory;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.entity.BukkitEVC;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.player.BukkitPVC;
import games.cubi.raycastedantiesp.paper.visibilitychangehandlers.tileentity.BukkitTVC;
import games.cubi.raycastedantiesp.paper.bStats.MetricsCollector;
import games.cubi.logs.Logger;

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
    //private boolean packetEventsPresent = false; // Don't use this to check if PacketEvents is present, use DataHolder's packetevents field instead. This just checks  if its present, not if its enabled/functional
    private static PacketProcessor packetProcessor = null;
    private static PaperSimpleEngine engine;
    private static MetricsCollector metricsCollector;
    private static RaycastedAntiESP instance;
    //todo: should probably rethink this entire class structure at some point. Too many static fields/methods. Also, a lot of the classes no longer need a reference to the main plugin class since Logger has been abstracted out and config could be given its own getter if needed
    {
        instance = this;
        Core.initialize(new PaperLoggerAdapter(getLogger()));
    }

    @Override
    public void onLoad() {
        config = ConfigManager.initialiseConfigManager(getResource("config.yml"), getDataFolder().toPath(), new PaperTileEntityConfig.Factory.FactoryProvider());
        Plugin packetEvents = Bukkit.getPluginManager().getPlugin("packetevents");
        if (packetEvents != null) {
            //packetEventsPresent = true;
            Logger.info("PacketEvents detected.", 5);
            new Registrar(this);

            PacketEventsStatus.init(true);
            packetProcessor = new PacketProcessor(RaycastedAntiESP.get());
        }
        else {
            Logger.info("PacketEvents not detected, disabling packet-based tablist modification. Don't worry, the plugin will still work without it.", 4);
            PacketEventsStatus.init(false);
        }
    }

    @Override
    public void onEnable() {
        /*
        //BukkitBSM blockSnapshotManager = new BukkitBSM(this, config);
        //BukkitESM entitySnapshotManager = new BukkitESM();*/
        PaperPESnapshotFactory snapshotFactory = new PaperPESnapshotFactory();
        SnapshotManager.initialise(snapshotFactory, snapshotFactory);
        VisibilityChangeHandlers.initialise(new BukkitEVC(), new BukkitPVC(), new BukkitTVC());

        tracker = new MovementTracker(this, config);
        engine = new PaperSimpleEngine(this, config);
        UpdateChecker.checkForUpdates(this, Bukkit.getConsoleSender());
        EventListener.initialise(this, packetProcessor, engine);

        initialiseCommands();

        //bStats
        metricsCollector =  new MetricsCollector(this, config);
    }

    @Override
    public void onDisable() {
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

    public static ConfigManager getConfigManager() {
        return config;
    }
    public static MovementTracker getMovementTracker() {
        return tracker;
    }
    public static PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }
    public static PaperSimpleEngine getEngine() {
        return engine;
    }
    public static RaycastedAntiESP get() {
        return instance;
    }
}