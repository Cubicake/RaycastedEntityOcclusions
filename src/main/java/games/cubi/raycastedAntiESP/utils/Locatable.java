package games.cubi.raycastedAntiESP.utils;


import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// This extends position but is not directly the position interface as it is marked unstable, so this way if the interface changes, only Locatable needs to be updated
public interface Locatable extends Position {
    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull FinePosition offset(double x, double y, double z) {
        throw new RuntimeException("Offset not implemented");
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull FinePosition offset(int x, int y, int z) {
        throw new RuntimeException("Offset not implemented");
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    default @NotNull BlockPosition toBlock() {
        throw new RuntimeException("not implemented");
    }

    Location toBukkitLocation();

    LocatableType getType();

    UUID world();

    enum LocatableType {
        Quantised,
        ThreadSafe,
        Block,
        Bukkit,
    }

    static Locatable convertLocatable(Locatable from, LocatableType to) {
        Locatable returnObject;
        switch (to) {
            case Quantised -> {
                //returnObject = new QuantisedLocation()
            }
            case ThreadSafe -> {
            }
            case Block -> {
            }
            case Bukkit -> {
            }
        }
        return returnObject;
    }

    static Locatable convertLocatable(Location from, LocatableType to) {
        convertLocatable()
    }
}
