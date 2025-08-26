package games.cubi.raycastedAntiESP.config;

public class PlayerConfig extends RaycastConfig {
    private final boolean onlyCullWhileSneaking;

    public PlayerConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.onlyCullWhileSneaking = onlyCullWhileSneaking;
    }

    public PlayerConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, boolean onlyCullWhileSneaking) {
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