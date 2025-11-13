package games.cubi.raycastedAntiESP.raycast;

import games.cubi.raycastedAntiESP.snapshot.ChunkSnapshotManager;
import games.cubi.raycastedAntiESP.utils.LocationPair;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class RaycastUtil {
    static Particle.DustOptions dustRed = null;
    static Particle.DustOptions dustGreen = null;
    static Particle.DustOptions dustBlue = null;

    private static void initDebugParticles() {
        if (dustRed == null) {
            dustRed = new Particle.DustOptions(Color.RED, 1f);
        }
        if (dustGreen == null) {
            dustGreen = new Particle.DustOptions(Color.GREEN, 1f);
        }
        if (dustBlue == null) {
            dustBlue = new Particle.DustOptions(Color.BLUE,1f);
        }
    }

    public static boolean raycastLocationPair(LocationPair locationPair, int maxOccluding, boolean debug, ChunkSnapshotManager snap) {

        //These locations do not need to be cloned because .toLocation creates a new location object. We can mutate it freely
        Location currentA = locationPair.first().toBukkitLocation();
        Location currentB = locationPair.second().toBukkitLocation();

        double total = currentA.distance(currentB);
        double traveledA = 0;
        double traveledB = 0;

        Vector dirA = currentB.toVector().subtract(currentA.toVector()).normalize();
        Vector dirB = currentA.toVector().subtract(currentB.toVector()).normalize();

        // Walk from each end until the cursors meet in the middle
        while (traveledA + traveledB < total) {

            /* --- step from A ---> B --- */
            currentA.add(dirA);
            traveledA += 1;
            Material matA = snap.getMaterialAt(currentA);
            if (matA != null && matA.isOccluding()) {
                maxOccluding--;
                if (debug) currentA.getWorld().spawnParticle(Particle.DUST, currentA, 1, dustRed);
            } else if (debug) {
                currentA.getWorld().spawnParticle(Particle.DUST, currentA, 1, dustGreen);
            }
            if (maxOccluding < 1) return false;

            /* --- step from B ---> A --- */
            if (traveledA + traveledB >= total) break; // already overlapped
            currentB.add(dirB);
            traveledB += 1;
            Material matB = snap.getMaterialAt(currentB);
            if (matB != null && matB.isOccluding()) {
                maxOccluding--;
                if (debug) currentB.getWorld().spawnParticle(Particle.DUST, currentB, 1, dustRed);
            } else if (debug) {
                currentB.getWorld().spawnParticle(Particle.DUST, currentB, 1, dustBlue);
            }
            if (maxOccluding < 1) return false;
        }
        return true;
    }


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