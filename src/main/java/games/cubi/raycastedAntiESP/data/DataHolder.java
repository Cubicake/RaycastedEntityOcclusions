package games.cubi.raycastedAntiESP.data;

public class DataHolder {

    private static boolean packetEventsPresent;

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
}
