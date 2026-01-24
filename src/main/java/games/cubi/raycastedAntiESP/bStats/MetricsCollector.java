package games.cubi.raycastedAntiESP.bStats;

import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.config.EntityConfig;
import games.cubi.raycastedAntiESP.config.PlayerConfig;
import games.cubi.raycastedAntiESP.config.RaycastConfig;
import games.cubi.raycastedAntiESP.config.TileEntityConfig;
import games.cubi.raycastedAntiESP.snapshot.SnapshotManager;
import games.cubi.raycastedAntiESP.snapshot.entity.BukkitESM;

public class MetricsCollector {
    private final RaycastedAntiESP plugin;
    private final Metrics metrics;
    private final ConfigManager config;

    public MetricsCollector(RaycastedAntiESP plugin, ConfigManager config) {
        this.plugin = plugin;
        int pluginId = 24553;
        metrics = new Metrics(plugin, pluginId);
        this.config = config;
        registerCustomMetrics();
    }

    public void shutdown() {
        metrics.shutdown();
    }
    public void registerCustomMetrics() {
        metrics.addCustomChart(new Metrics.SimplePie("max_occluding_count", this::getMaxOccludingCount));

        metrics.addCustomChart(new Metrics.SimplePie("cull_players", this::getCullPlayersStatus));

        metrics.addCustomChart(new Metrics.SimplePie("raycast_radius", () -> getRoundedValue(getRaycastConfig().getRaycastRadius(), getDefaultRaycastConfig().getRaycastRadius())));
        metrics.addCustomChart(new Metrics.SimplePie("always_show_radius", () -> getRoundedValue(getRaycastConfig().getAlwaysShowRadius(), getDefaultRaycastConfig().getAlwaysShowRadius())));

        metrics.addCustomChart(new Metrics.SimplePie("engine_mode", () -> String.valueOf(getRaycastConfig().getEngineMode())));

        metrics.addCustomChart(new Metrics.SimplePie("snapshot_refresh_interval", () -> getRoundedValue(config.getSnapshotConfig().getWorldSnapshotRefreshInterval(), ConfigManager.getDefaultSnapshotConfig().getWorldSnapshotRefreshInterval())));
        metrics.addCustomChart(new Metrics.SimplePie("entity_recheck_interval", () -> getRoundedValue(getRaycastConfig().getVisibleRecheckInterval(), getDefaultRaycastConfig().getVisibleRecheckInterval())));
        metrics.addCustomChart(new Metrics.SimplePie("tile_entity_recheck_interval", this::tileEntityCheckStatus));

        metrics.addCustomChart(new Metrics.SimplePie("server_size", this::getPlayersOnline));
        metrics.addCustomChart(new Metrics.SimplePie("entities", this::getEntities));
    }

    public String getCullPlayersStatus() {
        PlayerConfig playerConfig = config.getPlayerConfig();
        if (playerConfig.isEnabled()) {
            if (playerConfig.onlyCullWhileSneaking()) {
                return "Sneaking";
            } else {
                return "Always";
            }
        } else {
            return "Never";
        }
    }

    public String tileEntityCheckStatus() {
        TileEntityConfig tileEntityConfig = config.getTileEntityConfig();
        if (tileEntityConfig.isEnabled()) {
            return getRoundedValue(tileEntityConfig.getVisibleRecheckInterval(), ConfigManager.getDefaultTileEntityConfig().getVisibleRecheckInterval());
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

    public String getPlayersOnline() {
        int averaged = getSnapshotPlayerCount();
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
        int averaged = getSnapshotEntityCount();
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

    private RaycastConfig getRaycastConfig() {
        PlayerConfig playerConfig = config.getPlayerConfig();
        if (playerConfig.isEnabled()) {
            return playerConfig;
        }
        EntityConfig entityConfig = config.getEntityConfig();
        if (entityConfig.isEnabled()) {
            return entityConfig;
        }
        return config.getTileEntityConfig();
    }

    private RaycastConfig getDefaultRaycastConfig() {
        PlayerConfig playerDefaults = ConfigManager.getDefaultPlayerConfig();
        if (playerDefaults.isEnabled()) {
            return playerDefaults;
        }
        EntityConfig entityDefaults = ConfigManager.getDefaultEntityConfig();
        if (entityDefaults.isEnabled()) {
            return entityDefaults;
        }
        return ConfigManager.getDefaultTileEntityConfig();
    }

    private int getSnapshotPlayerCount() {
        BukkitESM snapshotManager = getEntitySnapshotManager();
        if (snapshotManager == null) {
            return -1;
        }
        return snapshotManager.getPlayerCount();
    }

    private int getSnapshotEntityCount() {
        BukkitESM snapshotManager = getEntitySnapshotManager();
        if (snapshotManager == null) {
            return -1;
        }
        return snapshotManager.getEntityCount();
    }

    private String getMaxOccludingCount() {
        return String.valueOf(getRaycastConfig().getMaxOccludingCount());
    }

    private BukkitESM getEntitySnapshotManager() {
        if (SnapshotManager.entitySnapshotManagerType() != SnapshotManager.EntitySnapshotManagerType.BUKKIT) {
            return null;
        }
        return (BukkitESM) SnapshotManager.getEntitySnapshotManager();
    }
}
