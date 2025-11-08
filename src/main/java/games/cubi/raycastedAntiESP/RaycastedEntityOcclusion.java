package games.cubi.raycastedAntiESP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import games.cubi.raycastedAntiESP.Packets.PacketProcessor;
import games.cubi.raycastedAntiESP.Packets.Registrar;
import games.cubi.raycastedAntiESP.Raycast.Engine;
import games.cubi.raycastedAntiESP.Raycast.MovementTracker;
import games.cubi.raycastedAntiESP.Snapshot.ChunkSnapshotManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import games.cubi.raycastedAntiESP.bStats.MetricsCollector;

public class RaycastedEntityOcclusion extends JavaPlugin implements CommandExecutor {
    private ConfigManager cfg;
    private ChunkSnapshotManager snapMgr;
    private MovementTracker tracker;
    private CommandsManager commands;
    private boolean packetEventsPresent = false;
    private PacketProcessor packetProcessor = null;

    public int tick = 0;

    @Override
    public void onLoad() {
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
        cfg = new ConfigManager(this);
        snapMgr = new ChunkSnapshotManager(this);
        tracker = new MovementTracker(this, cfg);
        commands = new CommandsManager(this, cfg);
        new UpdateChecker(this);
        getServer().getPluginManager().registerEvents(new EventListener(this, snapMgr, cfg), this);
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
                tick++;
                Engine.runEngine(cfg, snapMgr, tracker, RaycastedEntityOcclusion.this);
                Engine.runTileEngine(cfg, snapMgr, tracker, RaycastedEntityOcclusion.this);
            }
        }.runTaskTimer(this, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (packetEventsPresent && Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
                    cfg.setPacketEventsPresent(true);
                    packetProcessor = new PacketProcessor(RaycastedEntityOcclusion.this);
                    Logger.info("PacketEvents is enabled, enabling packet-based tablist modification.");
                }
            }
        }.runTaskLater(this, 1L);
    }


    public ConfigManager getConfigManager() {
        return cfg;
    }
    public ChunkSnapshotManager getChunkSnapshotManager() {
        return snapMgr;
    }
    public MovementTracker getMovementTracker() {
        return tracker;
    }
    public CommandsManager getCommandsManager() {
        return commands;
    }
    public PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }
}