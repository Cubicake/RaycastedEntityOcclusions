package games.cubi.raycastedEntityOcclusion.Raycast;

import games.cubi.raycastedEntityOcclusion.Snapshot.ChunkSnapshotManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;




public class RaycastUtil {
    public static boolean raycast(Location start, Location end, int maxOccluding, boolean debug, ChunkSnapshotManager snap) {
        Particle.DustOptions dustRed = null;
        Particle.DustOptions dustGreen = null;
        if (debug) {
            dustRed = new Particle.DustOptions(org.bukkit.Color.RED, 1f);
            dustGreen = new Particle.DustOptions(org.bukkit.Color.GREEN, 1f);
        }
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
            //System.out.println(curr + " " + mat);
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