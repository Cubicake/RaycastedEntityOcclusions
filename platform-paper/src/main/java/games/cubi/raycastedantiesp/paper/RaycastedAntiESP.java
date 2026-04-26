package games.cubi.raycastedantiesp.paper;

import games.cubi.raycastedantiesp.core.Core;
import games.cubi.raycastedantiesp.paper.commands.RaycastedAntiESPCommandBrigadier;
import games.cubi.raycastedantiesp.paper.engine.PaperSimpleEngine;
import games.cubi.raycastedantiesp.packetevents.config.PacketEventsBlockProcessorConfig;
import games.cubi.raycastedantiesp.packetevents.view.PacketEventsBlockView;
import games.cubi.raycastedantiesp.packetevents.view.PacketEventsEntityView;
import games.cubi.raycastedantiesp.core.view.ViewRegistry;
import games.cubi.raycastedantiesp.paper.packets.PaperPacketEventsBlockViewController;
import games.cubi.raycastedantiesp.paper.packets.PaperPacketEventsEntityViewController;
import games.cubi.raycastedantiesp.core.config.ConfigManager;
import games.cubi.raycastedantiesp.paper.bStats.MetricsCollector;
import games.cubi.logs.Logger;

import games.cubi.raycastedantiesp.paper.utils.FoliaTicker;
import games.cubi.raycastedantiesp.paper.utils.PaperTicker;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;
import java.util.List;

public final class RaycastedAntiESP extends JavaPlugin implements CommandExecutor {
    private static ConfigManager config;
    private static PaperPacketEventsEntityViewController packetEventsController;
    private static PaperSimpleEngine engine;
    private static MetricsCollector metricsCollector;
    private static RaycastedAntiESP instance;

    public static final boolean isFolia = getClass("io.papermc.paper.threadedregions.RegionizedServer") != null;
    //todo: should probably rethink this entire class structure at some point. Too many static fields/methods. Also, a lot of the classes no longer need a reference to the main plugin class since Logger has been abstracted out and config could be given its own getter if needed
    {
        instance = this;
        Core.initialize(new PaperLoggerAdapter(getLogger()));
    }

    public static @Nullable Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onLoad() {
        config = ConfigManager.initialiseConfigManager(
                () -> getResource("config.yml"),
                getDataFolder().toPath(),
                List.of(PacketEventsBlockProcessorConfig.EXTENSION)
        );
        Plugin packetEvents = Bukkit.getPluginManager().getPlugin("packetevents");
        if (packetEvents == null) {
            throw new IllegalStateException("PacketEvents is required but was not found.");
        }
        Logger.info("PacketEvents detected.", 5);
    }

    @Override
    public void onEnable() {
        IntSupplier currentTickSupplier;
        if (isFolia) {
            Logger.info("Folia detected. Some features may not work as expected.", 5);
            currentTickSupplier = new FoliaTicker();
        }
        else {
            currentTickSupplier = new PaperTicker();
        }
        ViewRegistry.initialise(PacketEventsBlockView::new, PacketEventsEntityView::new);
        packetEventsController = new PaperPacketEventsEntityViewController(currentTickSupplier);
        new PaperPacketEventsBlockViewController(currentTickSupplier);

        engine = new PaperSimpleEngine(this, config, currentTickSupplier);
        UpdateChecker.checkForUpdates(this, Bukkit.getConsoleSender());
        EventListener.initialise(this, engine, currentTickSupplier);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
        RaycastedAntiESPCommandBrigadier.register(event.registrar());
        }));
        //bStats
        metricsCollector =  new MetricsCollector(this, config);
    }

    @Override
    public void onDisable() {
        metricsCollector.shutdown();
    }


    public static ConfigManager getConfigManager() {
        return config;
    }
    public static PaperPacketEventsEntityViewController getPacketEventsController() {
        return packetEventsController;
    }
    public static PaperSimpleEngine getEngine() {
        return engine;
    }
    public static RaycastedAntiESP get() {
        return instance;
    }
}
