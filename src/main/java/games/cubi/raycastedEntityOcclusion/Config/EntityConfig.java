package games.cubi.raycastedEntityOcclusion.Config;

public class EntityConfig extends AbstractConfig {

    public EntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
    }

    public EntityConfig(boolean enabled) {
        super(enabled);
    }

}
