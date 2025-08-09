package games.cubi.raycastedEntityOcclusion.Config;

public class TileEntityConfig extends AbstractConfig {

    public TileEntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
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
