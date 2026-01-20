package games.cubi.raycastedEntityOcclusion;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    public int snapshotRefreshInterval;
    public int engineMode;
    public int maxOccludingCount;
    public boolean debugMode;
    public int alwaysShowRadius;
    public int raycastRadius;
    public int searchRadius;
    public boolean cullPlayers;
    public boolean cullPlayersOnJoin;
    public boolean onlyCullSneakingPlayers;
    public int recheckInterval;
    public boolean checkTileEntities;
    public int tileEntityRecheckInterval;
    public FileConfiguration cfg;
    public boolean packetEventsPresent = false;

    public static final int SNAPSHOT_REFRESH_INTERVAL_DEFAULT = 60;
    public static final int ENGINE_MODE_DEFAULT = 1;
    public static final int MAX_OCCLUDING_COUNT_DEFAULT = 3;
    public static final boolean DEBUG_MODE_DEFAULT = false;
    public static final int ALWAYS_SHOW_RADIUS_DEFAULT = 16;
    public static final int RAYCAST_RADIUS_DEFAULT = 48;
    public static final int SEARCH_RADIUS_DEFAULT = 48;
    public static final boolean CULL_PLAYERS_DEFAULT = false;
    public static final boolean CULL_PLAYERS_ON_JOIN_DEFAULT = false;
    public static final boolean ONLY_CULL_SNEAKING_PLAYERS_DEFAULT = false;
    public static final int RECHECK_INTERVAL_DEFAULT = 50;
    public static final boolean CHECK_TILE_ENTITIES_DEFAULT = true;
    public static final int TILE_ENTITY_RECHECK_INTERVAL_DEFAULT = 0;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        cfg = plugin.getConfig();

        snapshotRefreshInterval = cfg.getInt("snapshot-refresh-interval", SNAPSHOT_REFRESH_INTERVAL_DEFAULT);
        engineMode = cfg.getInt("engine-mode", ENGINE_MODE_DEFAULT);
        maxOccludingCount = cfg.getInt("max-occluding-count", MAX_OCCLUDING_COUNT_DEFAULT);
        debugMode = cfg.getBoolean("debug-mode", DEBUG_MODE_DEFAULT);

        alwaysShowRadius = cfg.getInt("always-show-radius", ALWAYS_SHOW_RADIUS_DEFAULT);
        raycastRadius = cfg.getInt("raycast-radius", RAYCAST_RADIUS_DEFAULT);
        searchRadius = cfg.getInt("search-radius", SEARCH_RADIUS_DEFAULT);
        cullPlayers = cfg.getBoolean("cull-players", CULL_PLAYERS_DEFAULT);
        cullPlayers = cfg.getBoolean("cull-players-on-join", CULL_PLAYERS_ON_JOIN_DEFAULT);
        onlyCullSneakingPlayers = cfg.getBoolean("only-cull-sneaking-players", ONLY_CULL_SNEAKING_PLAYERS_DEFAULT);
        recheckInterval = cfg.getInt("recheck-interval", RECHECK_INTERVAL_DEFAULT);

        checkTileEntities = cfg.getBoolean("check-tile-entities", CHECK_TILE_ENTITIES_DEFAULT);
        tileEntityRecheckInterval = cfg.getInt("tile-entity-recheck-interval", TILE_ENTITY_RECHECK_INTERVAL_DEFAULT);

        // Write defaults if missing
        cfg.addDefault("snapshot-refresh-interval", SNAPSHOT_REFRESH_INTERVAL_DEFAULT);
        cfg.addDefault("engine-mode", ENGINE_MODE_DEFAULT);
        cfg.addDefault("max-occluding-count", MAX_OCCLUDING_COUNT_DEFAULT);
        cfg.addDefault("debug-mode", DEBUG_MODE_DEFAULT);
        cfg.addDefault("always-show-radius", ALWAYS_SHOW_RADIUS_DEFAULT);
        cfg.addDefault("raycast-radius", RAYCAST_RADIUS_DEFAULT);
        cfg.addDefault("search-radius", SEARCH_RADIUS_DEFAULT);
        cfg.addDefault("cull-players", CULL_PLAYERS_DEFAULT);
        cfg.addDefault("cull-players-on-join", CULL_PLAYERS_ON_JOIN_DEFAULT);
        cfg.addDefault("only-cull-sneaking-players", ONLY_CULL_SNEAKING_PLAYERS_DEFAULT);
        cfg.addDefault("recheck-interval", RECHECK_INTERVAL_DEFAULT);
        cfg.addDefault("check-tile-entities", CHECK_TILE_ENTITIES_DEFAULT);
        cfg.addDefault("tile-entity-recheck-interval", TILE_ENTITY_RECHECK_INTERVAL_DEFAULT);
        cfg.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public int setConfigValue(String path, String rawValue) {
        if (!cfg.contains(path)) return -1;
        Object current = cfg.get(path);
        Object parsed;
        if (current instanceof Boolean) {
            String lower = rawValue.toLowerCase();
            if (!lower.equals("true") && !lower.equals("false")) return -1;
            parsed = Boolean.parseBoolean(lower);
        } else if (current instanceof Number) {
            int intVal;
            try {
                intVal = Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                return -1;
            }
            if (intVal < 0 || intVal > 256) return 0;
            parsed = intVal;
        } else {
            return -1;
        }
        cfg.set(path, parsed);
        plugin.saveConfig();
        load();
        return 1;
        /*
        -1 = invalid input
        0 = out of range
        1 = success
         */
    }
    public void setPacketEventsPresent(boolean present) {
        this.packetEventsPresent = present;
    }
}