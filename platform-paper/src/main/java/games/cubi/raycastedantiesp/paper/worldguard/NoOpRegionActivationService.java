package games.cubi.raycastedantiesp.paper.worldguard;

import games.cubi.locatables.Locatable;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class NoOpRegionActivationService implements RegionActivationService {
    private final BooleanSupplier defaultEnabledSupplier;

    public NoOpRegionActivationService(BooleanSupplier defaultEnabledSupplier) {
        this.defaultEnabledSupplier = Objects.requireNonNull(defaultEnabledSupplier, "defaultEnabledSupplier");
    }

    @Override
    public boolean isEnabled(Locatable locatable) {
        return locatable != null && locatable.world() != null && defaultEnabledSupplier.getAsBoolean();
    }

    @Override
    public void addListener(Listener listener) {
    }

    @Override
    public void removeListener(Listener listener) {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
