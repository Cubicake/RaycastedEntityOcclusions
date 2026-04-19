package games.cubi.raycastedantiesp.paper.worldguard;

import games.cubi.locatables.Locatable;

import java.util.UUID;

public interface RegionActivationService {
    boolean isEnabled(Locatable locatable);

    default boolean isEnabled(Locatable first, Locatable second) {
        return isEnabled(first) && isEnabled(second);
    }

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void start();

    void stop();

    interface Listener {
        void onPlayerEnteredEnabledRegion(UUID playerUUID);

        void onPlayerExitedEnabledRegion(UUID playerUUID);
    }
}
