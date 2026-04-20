package games.cubi.raycastedantiesp.paper.utils;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.function.IntSupplier;

public class PaperTicker extends PaperListener implements IntSupplier {
    private volatile int currentTick = 0;

    public PaperTicker() {}

    @Override
    public int getAsInt() {
        return currentTick;
    }

    @EventHandler(priority = EventPriority.LOWEST) //Runs first
    public void serverTickStartEvent(ServerTickStartEvent event) {
        currentTick++;
    }
}
