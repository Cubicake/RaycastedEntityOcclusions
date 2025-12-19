package games.cubi.raycastedAntiESP.data;

public class DataHolder {

    private static int tick = 0;

    private static final EntityVisibilityChangeCache entityVisibilityChangeCache;
    private static final PlayerRegistry playerRegistry;

    static {
        entityVisibilityChangeCache = EntityVisibilityChangeCache.getInstance();
        playerRegistry = PlayerRegistry.getInstance();
    }

    public static EntityVisibilityChangeCache entityVisibility() {
        return entityVisibilityChangeCache;
    }
    public static PlayerRegistry players() {
        return playerRegistry;
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
