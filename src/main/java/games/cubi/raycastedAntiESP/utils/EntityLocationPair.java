package games.cubi.raycastedAntiESP.utils;

import org.bukkit.Location;

import java.util.UUID;

public class EntityLocationPair {
    private final UUID entity;
    private final Location loc;
    EntityLocationPair(UUID entity, Location loc) {
        this.entity = entity;
        this.loc = loc;
    }
    Location getLoc() {return loc;}
    UUID getEntity() {return entity;}
}
