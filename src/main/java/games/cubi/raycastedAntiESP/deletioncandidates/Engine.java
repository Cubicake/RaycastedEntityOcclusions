package games.cubi.raycastedAntiESP.deletioncandidates;

@Deprecated(forRemoval = true)
public class Engine {}
/*
import games.cubi.raycastedEntityOcclusion.ConfigManager;
import games.cubi.raycastedEntityOcclusion.RaycastedEntityOcclusion;
import games.cubi.raycastedEntityOcclusion.Utils.*;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Engine {
    //TODO: Maybe store the data somewhere else? Could rework chunk storage to be general data storage
    public ConcurrentHashMap<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final AsyncScheduler asyncScheduler;
    @SuppressWarnings("FieldCanBeLocal")
    private final GlobalRegionScheduler globalScheduler; //TODO: Note that the use of the global scheduler here probs isn't actually done in a folia compat way

    private final RaycastedEntityOcclusion plugin;
    private final ConfigManager config;

    public Engine(RaycastedEntityOcclusion plugin, ConfigManager cfg) {
        this.plugin = plugin;
        this.config = cfg;
        asyncScheduler = plugin.getServer().getAsyncScheduler();
        globalScheduler = plugin.getServer().getGlobalRegionScheduler();

        globalScheduler.runAtFixedRate(plugin, syncCollectDataFromBukkit(), 1, 1);
    }

    public void registerPlayer(UUID playerUUID, boolean bypass) {
        playerDataMap.putIfAbsent(playerUUID, new PlayerData(playerUUID, bypass));
    }


    private Consumer<ScheduledTask> syncCollectDataFromBukkit() {
        // UUID:World UUID -> UUID:Entity UUID -> QuantisedLocation
        HashMap<UUID, HashMap<UUID, QuantisedLocation>> worldEntities = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            HashMap<UUID, QuantisedLocation> entitiesInWorld = new HashMap<>();
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player) continue; // Skip players, they are handled separately
                entitiesInWorld.put(entity.getUniqueId(), new QuantisedLocation(entity.getLocation(), entity.getHeight()));
            }
            worldEntities.put(world.getUID(), entitiesInWorld);
        }
        HashMap<UUID, QuantisedLocation> players = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.put(player.getUniqueId(), new QuantisedLocation(player.getLocation(), player.getEyeHeight()));
        }





//TODO: This is old
        HashMap<UUID, HashMap<UUID, Location>> gatheredData = new HashMap<>();

        for (UUID playerUUID : playerDataMap.keySet()) {
            PlayerData data = playerDataMap.get(playerUUID);

            if (data.hasBypassPermission()) continue;

            Map<UUID, Boolean> entities = data.getEntityVisibilityMap();
            Map<BlockLocation, Long> tileEntities = data.getSeenTileEntitiesMap();

            HashMap<UUID, Location> entitiesToCheck = new HashMap<>();

            data.incrementTicksSinceVisibleEntityRecheck();
            if (data.getTicksSinceVisibleEntityRecheck() >= config.recheckInterval) {
                data.resetTicksSinceVisibleEntityRecheck();
                for (UUID entityUUID : entities.keySet()) {
                    entitiesToCheck.putIfAbsent(entityUUID, Bukkit.getEntity(entityUUID).getLocation());
                }
            }
            else {
                for (Map.Entry<UUID, Boolean> entry : entities.entrySet()) {
                    if (!entry.getValue()) {
                        UUID uuid = entry.getKey();
                        entitiesToCheck.putIfAbsent(uuid, Bukkit.getEntity(uuid).getLocation());
                    }
                }
            }
            entitiesToCheck.put(playerUUID, Bukkit.getPlayer(playerUUID).getLocation()); //used to pass in player location
            gatheredData.put(playerUUID, entitiesToCheck);
        }

        asyncScheduler.runNow(plugin, processData(gatheredData));

        return task -> {};
    }

    private Consumer<ScheduledTask> processData(HashMap<UUID, HashMap<UUID, Location>> gatheredData) {

        HashMap<LocationPair, Subscribers> raycastResultSubscription = new HashMap<>();

        for (Map.Entry<UUID, HashMap<UUID, Location>> data : gatheredData.entrySet()) {
            UUID player = data.getKey();
            HashMap<UUID, Location> entityLocations = data.getValue();
            QuantisedLocation playerLoc = new QuantisedLocation(entityLocations.get(player));

            for (Map.Entry<UUID, Location> entity : entityLocations.entrySet()) {

                QuantisedLocation entityLoc = new QuantisedLocation(entity.getValue());

                if (!playerLoc.isWithinRadius(entityLoc, config.raycastRadius)) continue;

                LocationPair locationPair = LocationPair.of(playerLoc, entityLoc);

                if (raycastResultSubscription.containsKey(locationPair)) {
                    raycastResultSubscription.get(locationPair).addSubscriberWithEntity(player, entity.getKey());
                }
                else {
                    raycastResultSubscription.put(locationPair, new Subscribers(player, entity.getKey()));
                }
            }
        }

        return task -> {};
    }

}
*/