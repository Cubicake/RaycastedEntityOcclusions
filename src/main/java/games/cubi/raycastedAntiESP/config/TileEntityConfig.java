package games.cubi.raycastedAntiESP.config;

public class TileEntityConfig extends RaycastConfig {

    public TileEntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public TileEntityConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public TileEntityConfig(boolean enabled) {
        super(enabled);
    }

    @Override
    public int getVisibleRecheckIntervalSeconds() {
        return super.getVisibleRecheckInterval();
    }
}
