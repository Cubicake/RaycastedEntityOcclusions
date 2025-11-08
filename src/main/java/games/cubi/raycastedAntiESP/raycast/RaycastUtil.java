package games.cubi.raycastedAntiESP.raycast;

import games.cubi.raycastedAntiESP.snapshot.ChunkSnapshotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class RaycastUtil {
    static Particle.DustOptions dustRed = null;
    static Particle.DustOptions dustGreen = null;

    private static void initDebugParticles() {
        if (dustRed == null) {
            dustRed = new Particle.DustOptions(org.bukkit.Color.RED, 1f);
        }
        if (dustGreen == null) {
            dustGreen = new Particle.DustOptions(org.bukkit.Color.GREEN, 1f);
        }
    }

    public static boolean raycast(Location start, Location end, int maxOccluding, boolean debug, ChunkSnapshotManager snap) {
        initDebugParticles();
        if (!start.getWorld().equals(end.getWorld())) return false;
        double total = start.distance(end);
        double traveled = 0;
        Location curr = start.clone();
        Vector dir = end.toVector().subtract(start.toVector()).normalize();
        while (traveled < total) {
            curr.add(dir);
            traveled += 1;
            Material mat = snap.getMaterialAt(curr);
            if (mat == null) {
                continue;
            }
            if (mat.isOccluding()) {
                maxOccluding--;
                if (debug) {
                    start.getWorld().spawnParticle(Particle.DUST, curr, 1, dustRed);
                }
                if (maxOccluding < 1) return false;
            }
            else if (debug) {
                start.getWorld().spawnParticle(Particle.DUST, curr, 1, dustGreen);
            }
        }
        return true;
    }
}