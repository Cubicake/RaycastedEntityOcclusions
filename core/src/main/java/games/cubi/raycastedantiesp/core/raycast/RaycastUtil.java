package games.cubi.raycastedantiesp.core.raycast;

import games.cubi.locatables.Locatable;
import games.cubi.locatables.MutableLocatable;
import games.cubi.locatables.implementations.MutableBlockVector;
import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.players.PlayerData;
import games.cubi.raycastedantiesp.core.snapshot.PlayerBlockSnapshotManager;

public class RaycastUtil {

//True: Has line-of-sight
    public static boolean raycast(PlayerData player, Locatable start, Locatable end, int maxOccluding, int alwaysShowRadius, int maxRaycastRadius, boolean debug, PlayerBlockSnapshotManager snap, int stepSize, ParticleSpawner particleSpawner) {
        if (!start.world().equals(end.world())) return false;

        MutableLocatable clonedEnd = end.clonePlainAndCentreIfBlockLocation();
        double total = start.distance(clonedEnd); //benchmarking shows that calling distance() is faster than distanceSquared() then checking distanceSquared < stepSize*stepSize every time despite the latter replacing a square root with multiplication
        if (total <= alwaysShowRadius) return true;
        if (total > maxRaycastRadius) return false;
        if (debug && particleSpawner == null) {
            Logger.errorAndReturn(new RuntimeException("raycast called with debug enabled but no ParticleSpawner supplied"), 2, RaycastUtil.class);
        }

        Locatable dir = clonedEnd.subtract(start).normalize().scalarMultiply(stepSize);

        MutableBlockVector current = new MutableBlockVector(start.world(), start.x(),start.y(),start.z());

        for (double traveled = 0; traveled < total; traveled += stepSize) { //benchmarking shows that for loop is marginally faster than while loop initially (after running for a while they are equal
            current.add(dir);

            if (snap.isBlockOccluding(current)) {//This works as MutableBlockVector resolves to a block location in #equals and #hashCode, and thus works fine as a key in the snapshot manager
                maxOccluding--;
                if (debug) particleSpawner.spawnParticleAt(current, ParticleSpawner.Colour.RED);
                if (maxOccluding < 1) return false;
                continue;
            }

            if (debug) particleSpawner.spawnParticleAt(current, ParticleSpawner.Colour.GREEN);
        }
        return true;
    }
}
