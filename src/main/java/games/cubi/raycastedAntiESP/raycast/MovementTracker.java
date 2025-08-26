package games.cubi.raycastedAntiESP.raycast;

import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.RaycastedAntiESP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovementTracker {
    private final Map<Player, Deque<Location>> history = new ConcurrentHashMap<>();

    public MovementTracker(RaycastedAntiESP plugin, ConfigManager config) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, updateLocationHistory(config), 1L, 1L);
    }

    private Runnable updateLocationHistory(ConfigManager config) {
        if (config.getEngineMode() != 2) return () -> {};
        for (Player p : Bukkit.getOnlinePlayers()) {
            history.computeIfAbsent(p, k -> new ArrayDeque<>(5));
            Deque<Location> dq = history.get(p);

            if (dq.size() >= 5) dq.removeFirst();

            dq.addLast(p.getEyeLocation());
        }
        return () -> {};
    }

    /**
     * Predicts location 5 ticks ahead based on last 5 ticks. Returns null if insufficient or too slow.
     */
    public Location getPredictedLocation(Player p) {
        Deque<Location> dq = history.get(p);
        if (dq == null || dq.size() < 5) return null;
        Location old = dq.peekFirst();
        Location now = dq.peekLast();
        if (old == null || now == null) return null;
        double dx = now.getX() - old.getX();
        double dy = now.getY() - old.getY();
        double dz = now.getZ() - old.getZ();
        double speed = Math.sqrt(dx*dx + dy*dy + dz*dz) / 5.0;
        if (speed < 0.1) return null;
        return now.clone().add(dx*2, dy, dz*2);
    }
}