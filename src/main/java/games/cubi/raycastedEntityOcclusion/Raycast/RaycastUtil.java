package games.cubi.raycastedEntityOcclusion.Raycast;

import games.cubi.raycastedEntityOcclusion.Snapshot.ChunkSnapshotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class RaycastUtil {
    private static final Particle.DustOptions DUST_RED = new Particle.DustOptions(org.bukkit.Color.RED, 1f);
    private static final Particle.DustOptions DUST_GREEN = new Particle.DustOptions(org.bukkit.Color.GREEN, 1f);;

    public static boolean raycast(Location start, Location end, int maxOccluding, boolean debug, ChunkSnapshotManager snap) {
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
                    start.getWorld().spawnParticle(Particle.DUST, curr, 1, DUST_RED);
                }
                if (maxOccluding < 1) return false;
            }
            else if (debug) {
                start.getWorld().spawnParticle(Particle.DUST, curr, 1, DUST_GREEN);
            }
        }
        return true;
    }
}