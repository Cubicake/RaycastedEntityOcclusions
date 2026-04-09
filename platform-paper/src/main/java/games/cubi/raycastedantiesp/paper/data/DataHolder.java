package games.cubi.raycastedantiesp.paper.data;

import games.cubi.raycastedantiesp.core.players.PlayerRegistry;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated(forRemoval = true)
public class DataHolder {

    private static AtomicInteger tick = new AtomicInteger();

    private static final PlayerRegistry playerRegistry;

    static {
        playerRegistry = PlayerRegistry.getInstance();
    }

    public static PlayerRegistry players() {
        return playerRegistry;
    }

    public static int getTick() {
        return tick.get();
    }
    /**
     * This method should ONLY be called on the tick start event
     * **/
    public static void incrementTick() {
        tick.incrementAndGet();
    }
}
