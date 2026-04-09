package games.cubi.raycastedantiesp.core.config.raycast;

import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

/**
 * @param <GenericBlock> The block type, e.g. Material for Bukkit, int for Packets
 */
public abstract class PlatformTileEntityConfig<GenericBlock> extends RaycastConfig {

    private static final String PATH = "checks.tile-entity";
    protected final List<GenericBlock> exemptedBlocks; // This list is immutable

    public PlatformTileEntityConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, List<GenericBlock> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public PlatformTileEntityConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, List<GenericBlock> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public PlatformTileEntityConfig(boolean enabled, List<GenericBlock> exemptedBlocks) {
        super(enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public PlatformTileEntityConfig(RaycastConfig superConfig, List<GenericBlock> exemptedBlocks) {
        super(superConfig);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    @Override
    public int getVisibleRecheckIntervalSeconds() {
        return super.getRawVisibleRecheckInterval();
    }

    @Override
    public short getVisibleRecheckIntervalTicks() {
        return (short) (super.getRawVisibleRecheckInterval() * 20);
    }

    public List<GenericBlock> getExemptedBlocks() {
        return exemptedBlocks; // Immutable list
    }

    public static abstract class Factory<T extends PlatformTileEntityConfig<?>, GenericBlock> extends RaycastConfig.Factory<T> {
        protected static final String EXEMPTED_BLOCKS_PATH = PATH + ".exempted-blocks";

        public Factory() {
            super(PATH);
        }

        public abstract @NotNull T getDefaults();
        protected abstract List<String> getBlockNameList();

        protected abstract List<GenericBlock> getExemptedBlockList(ConfigurationNode config);

        public @NotNull Factory<T, GenericBlock> setDefaults(ConfigurationNode config) {
            super.setDefaults(config, getDefaults());
            //ConfigNodeUtil.addDefault(config, EXEMPTED_BLOCKS_PATH, defaults.getExemptedBlocks());
            ConfigNodeUtil.addDefault(config, EXEMPTED_BLOCKS_PATH, getBlockNameList());
            return this;
        }

        public interface FactoryProvider<F extends Factory<?, ?>> {
            F getFactory();
        }
    }
}