package games.cubi.raycastedAntiESP.raycast;

import games.cubi.raycastedAntiESP.Logger;
import games.cubi.raycastedAntiESP.locatables.block.MutableBlockVector;
import games.cubi.raycastedAntiESP.snapshot.block.BlockSnapshotManager;
import games.cubi.raycastedAntiESP.locatables.Locatable;
import games.cubi.raycastedAntiESP.utils.LocationPair;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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

    public static boolean raycastLocationPair(LocationPair locationPair, int maxOccluding, boolean debug, BlockSnapshotManager snap) {

        //These locations do not need to be cloned because .toLocation creates a new location object. We can mutate it freely
        Location currentA = null;//locationPair.first().toBukkitLocation();
        Location currentB = null;//locationPair.second().toBukkitLocation();

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
            /*Material matA = snap.getMaterialAt(WrappedBukkitLocation.wrap(currentA));
            if (matA != null && matA.isOccluding()) {
                maxOccluding--;
                if (debug) currentA.getWorld().spawnParticle(Particle.DUST, currentA, 1, dustRed);
            } else if (debug) {
                currentA.getWorld().spawnParticle(Particle.DUST, currentA, 1, dustGreen);
            }
            if (maxOccluding < 1) return false;

            /* --- step from B ---> A --- */
            /*if (traveledA + traveledB >= total) break; // already overlapped
            currentB.add(dirB);
            traveledB += 1;
            Material matB = snap.getMaterialAt(WrappedBukkitLocation.wrap(currentB)); //todo dont do this many pointless wraps
            if (matB != null && matB.isOccluding()) {
                maxOccluding--;
                if (debug) currentB.getWorld().spawnParticle(Particle.DUST, currentB, 1, dustRed);
            } else if (debug) {
                currentB.getWorld().spawnParticle(Particle.DUST, currentB, 1, dustBlue);
            }
            if (maxOccluding < 1) return false;*/
        }
        return true;
    }

//True: Has line-of-sight
    public static boolean raycast(Locatable start, Locatable end, int maxOccluding, int alwaysShowRadius, int maxRaycastRadius, boolean debug, BlockSnapshotManager snap, int stepSize) {
        if (!start.world().equals(end.world())) return false;

        Locatable clonedEnd = end.clonePlainAndCentreIfBlockLocation();
        double total = start.distance(clonedEnd); //benchmarking shows that calling distance() is faster than distanceSquared() then checking distanceSquared < stepSize*stepSize every time despite the latter replacing a square root with multiplication
        if (total <= alwaysShowRadius) return true;
        if (total > maxRaycastRadius) return false;

        World world = null;
        Location currentLocation = null;

        if (debug) {
            initDebugParticles();
            world = Bukkit.getWorld(start.world());
            currentLocation = new Location(world, start.x(), start.y(), start.z());
            if (world == null) {
                Logger.errorAndReturn(new RuntimeException("RaycastUtil.raycast: world is null for UUID " + start.world()), 2);
                debug = false; //code will exit before this point, this is to shut up the warnings
            }
        }

        Locatable dir = clonedEnd.subtract(start).normalize().scalarMultiply(stepSize); // Locatable may be instance of immutable BlockLocation, so convert to Plain first

        MutableBlockVector current = new MutableBlockVector(start.world(), start.x(),start.y(),start.z());

        for (double traveled = 0; traveled < total; traveled += stepSize) { //benchmarking shows that for loop is marginally faster than while loop initially (after running for a while they are equal
            current.add(dir);
            if (debug) currentLocation.set(current.x(), current.y(), current.z());

            if (snap.isBlockOccluding(current)) {//This works as MutableBlockVector resolves to a block location in #equals and #hashCode, and thus works fine as a key in the snapshot manager
                maxOccluding--;
                if (debug) world.spawnParticle(Particle.DUST, currentLocation, 1, dustRed);
                if (maxOccluding < 1) return false;
                continue;
            }

            if (debug) world.spawnParticle(Particle.DUST, currentLocation, 1, dustGreen);
        }
        return true;
    }
}