package games.cubi.raycastedantiesp.paper.utils;

import games.cubi.raycastedantiesp.paper.RaycastedAntiESP;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;

public class FoliaTicker implements IntSupplier {
    private final AtomicInteger tick = new AtomicInteger(0);

    public FoliaTicker() {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(RaycastedAntiESP.get(), this::increment, 0L, 1L); //Is this guaranteed to be a specific thread?
    }

    private void increment(ScheduledTask scheduledTask) {
        tick.incrementAndGet();
    }

    @Override
    public int getAsInt() {
        return tick.get();
    }
}
