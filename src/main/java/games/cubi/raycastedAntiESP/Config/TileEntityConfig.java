package games.cubi.raycastedAntiESP.Config;

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
    public short getVisibleRecheckIntervalSeconds() {
        return super.getVisibleRecheckInterval();
    }
}
