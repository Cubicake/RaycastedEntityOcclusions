package games.cubi.raycastedEntityOcclusion.Config;

public class PlayerConfig extends AbstractConfig {
    private final boolean onlyCullWhileSneaking;

    public PlayerConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public PlayerConfig(boolean enabled) {
        super(enabled);
        this.onlyCullWhileSneaking = false;
    }

    public boolean onlyCullWhileSneaking() {
        return onlyCullWhileSneaking;
    }
}