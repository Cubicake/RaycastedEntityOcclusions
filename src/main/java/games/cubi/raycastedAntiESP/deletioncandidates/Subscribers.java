package games.cubi.raycastedAntiESP.deletioncandidates;

import java.util.*;

@Deprecated(forRemoval = true)
public class Subscribers {
    //First UUID are the players, second uuid are the entities tied to their subscription
    private final HashMap<UUID, Set<UUID>> subscribers = new HashMap<>();

    public Subscribers(UUID player, Set<UUID> entities) {
        subscribers.putIfAbsent(player, entities);
    }

    public Subscribers(UUID player, UUID entity) {
        addSubscriberWithEntity(player, entity);
    }

    public Subscribers() {}

    public void addSubscriberWithEntity(UUID player, UUID entity) {
        // if 'player' is not already in the map, create a new HashSet<>()
        subscribers
                .computeIfAbsent(player, k -> new HashSet<>())
                .add(entity);
    }

    public Set<UUID> getEntities(UUID player) {
        return subscribers.getOrDefault(player, Collections.emptySet());
    }

    public Set<UUID> getPlayers() {
        return subscribers.keySet();
    }
}
