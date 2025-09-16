package games.cubi.raycastedAntiESP.data;

public class DataHolder {

    private static boolean packetEventsPresent;
    private static int tick = 0;

    private static final EntityLocationService entityLocationService;
    private static final EntityVisibilityChangeCache entityVisibilityChangeCache;
    private static final PlayerRegistry playerRegistry;

    static {
        entityLocationService = EntityLocationService.getInstance();
        entityVisibilityChangeCache = EntityVisibilityChangeCache.getInstance();
        playerRegistry = PlayerRegistry.getInstance();
    }

    public static EntityLocationService entityLocation() {
        return entityLocationService;
    }
    public static EntityVisibilityChangeCache entityVisibility() {
        return entityVisibilityChangeCache;
    }
    public static PlayerRegistry players() {
        return playerRegistry;
    }

    public static boolean isPacketEventsPresent() {
        return packetEventsPresent;
    }
    public static void setPacketEventsPresent() {
        packetEventsPresent = true;
    }
    /**
     * This counter increments twice per server tick, so a normal server will experience 40 TPS
     * **/
    public static int getTick() {
        return tick;
    }
    /**
     * This method should ONLY be called on the tick start and stop events
     * **/
    public static void incrementTick() {
        tick++;
    }
}
