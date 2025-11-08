package games.cubi.raycastedAntiESP.bStats;

import games.cubi.raycastedAntiESP.ConfigManager;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class MetricsCollector {
    private final RaycastedAntiESP plugin;
    private final Metrics metrics;
    private final ConfigManager config;

    private List<Integer> playersOnline = new ArrayList<>();
    private List<Integer> entities = new ArrayList<>();

    public MetricsCollector(RaycastedAntiESP plugin, ConfigManager config) {
        this.plugin = plugin;
        int pluginId = 24553;
        metrics = new Metrics(plugin, pluginId);
        this.config = config;
        registerCustomMetrics();

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::collectMetrics, 0L, 6000L); // 6000 ticks = 5 minutes
    }

    public void registerCustomMetrics() {
        metrics.addCustomChart(new Metrics.SimplePie("max_occluding_count", () -> String.valueOf(config.maxOccludingCount)));

        metrics.addCustomChart(new Metrics.SimplePie("cull_players", this::getCullPlayersStatus));

        metrics.addCustomChart(new Metrics.SimplePie("raycast_radius", () -> getRoundedValue(config.raycastRadius, ConfigManager.RAYCAST_RADIUS_DEFAULT)));
        metrics.addCustomChart(new Metrics.SimplePie("search_radius", () -> getRoundedValue(config.searchRadius, ConfigManager.SEARCH_RADIUS_DEFAULT)));
        metrics.addCustomChart(new Metrics.SimplePie("always_show_radius", () -> getRoundedValue(config.alwaysShowRadius, ConfigManager.ALWAYS_SHOW_RADIUS_DEFAULT)));

        metrics.addCustomChart(new Metrics.SimplePie("engine_mode", () -> String.valueOf(config.engineMode)));

        metrics.addCustomChart(new Metrics.SimplePie("snapshot_refresh_interval", () -> getRoundedValue(config.snapshotRefreshInterval, ConfigManager.SNAPSHOT_REFRESH_INTERVAL_DEFAULT)));
        metrics.addCustomChart(new Metrics.SimplePie("entity_recheck_interval", () -> getRoundedValue(config.recheckInterval, ConfigManager.RECHECK_INTERVAL_DEFAULT)));
        metrics.addCustomChart(new Metrics.SimplePie("tile_entity_recheck_interval", this::tileEntityCheckStatus));

        metrics.addCustomChart(new Metrics.SimplePie("server_size", this::getPlayersOnline));
        metrics.addCustomChart(new Metrics.SimplePie("entities", this::getEntities));
    }

    public String getCullPlayersStatus() {
        if (config.cullPlayers) {
            if (config.onlyCullSneakingPlayers) {
                return "Sneaking";
            } else {
                return "Always";
            }
        } else {
            return "Never";
        }
    }

    public String tileEntityCheckStatus() {
        if (config.checkTileEntities) {
            return getRoundedValue(config.tileEntityRecheckInterval, ConfigManager.TILE_ENTITY_RECHECK_INTERVAL_DEFAULT);
        } else {
            return "Disabled";
        }
    }

    public String getRoundedValue(int value, int defaultValue) {
        if (value == defaultValue) {
            return defaultValue + ".0";
        } else {
            int roundedValue = Math.round(value / 5.0f) * 5;
            return String.valueOf(roundedValue);
        }
    }

    public void collectMetrics() {
        playersOnline.add(Bukkit.getServer().getOnlinePlayers().size());
        // TODO: Cache all entities in Engine, and also use that here
        int totalEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            totalEntities += world.getEntities().size();
        }
        entities.add(totalEntities);
    }
    public String getPlayersOnline() {
        if (playersOnline == null) {
            return "Null";
        }
        int averaged = (int) playersOnline.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(-1);
        playersOnline.clear();

        if (averaged < 0) {
            return "Null";
        } else if (averaged < 4) {
            return String.valueOf(averaged);
        } else if (averaged < 7) {
            return "4-6";
        } else if (averaged < 11) {
            return "7-10";
        } else if (averaged < 16) {
            return "11-15";
        } else if (averaged < 26) {
            return "15-25";
        } else if (averaged < 40) {
            return "26-40";
        } else if (averaged < 71) {
            return "41-70";
        } else if (averaged < 101) {
            return "71-100";
        } else if (averaged < 201) {
            return "101-200";
        } else if (averaged < 301) {
            return "201-300";
        } else if (averaged < 501) {
            return "301-500";
        } else {
            return "500+";
        }

    }
    public String getEntities() {
        if (entities == null) {
            return "Null";
        }
        int averaged = (int) entities.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(-1);
        entities.clear();

        if (averaged < 0) {
            return "Null";
        } else if (averaged < 21) {
            return "0-20";
        } else if (averaged < 51) {
            return "21-50";
        } else if (averaged < 101) {
            return "51-100";
        } else if (averaged < 301) {
            return "101-300";
        } else if (averaged < 501) {
            return "301-500";
        } else if (averaged < 1001) {
            return "501-1000";
        } else if (averaged < 2001) {
            return "1001-2000";
        } else if (averaged < 5000) {
            return "2001-5000";
        } else {
            return "5000+";
        }
    }
}
