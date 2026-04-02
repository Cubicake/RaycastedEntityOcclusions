package games.cubi.raycastedantiesp.paper.config;

import games.cubi.logs.Logger;
import games.cubi.raycastedantiesp.core.config.ConfigNodeUtil;
import games.cubi.raycastedantiesp.core.config.raycast.PlatformTileEntityConfig;
import games.cubi.raycastedantiesp.core.config.raycast.RaycastConfig;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class PaperTileEntityConfig extends PlatformTileEntityConfig<Material> {
    private static final String PATH = "checks.tile-entity";

    public PaperTileEntityConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled, exemptedBlocks);
    }

    public PaperTileEntityConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled, exemptedBlocks);
    }

    public PaperTileEntityConfig(boolean enabled, List<Material> exemptedBlocks) {
        super(enabled, exemptedBlocks);
    }

    public PaperTileEntityConfig(RaycastConfig superConfig,List<Material> exemptedBlocks) {
        super(superConfig, exemptedBlocks);
    }

    public List<Material> getExemptedBlocks() {
        return exemptedBlocks; // Immutable list
    }

    public static class Factory extends PlatformTileEntityConfig.Factory<PaperTileEntityConfig, Material> {

        private static final List<Material> DEFAULT_EXEMPTED_BLOCKS = List.of(Material.BEACON);
        public static final PaperTileEntityConfig DEFAULT = new PaperTileEntityConfig(3, 1, 48, 10, true, DEFAULT_EXEMPTED_BLOCKS);


        public Factory() {}

        @Override
        public @NotNull PaperTileEntityConfig getDefaults() {
            return DEFAULT;
        }

        @Override
        public List<Material> getExemptedBlockList(ConfigurationNode config) {
            List<String> materialNames = ConfigNodeUtil.getStringList(config, EXEMPTED_BLOCKS_PATH);

            ArrayList<Material> materials = new ArrayList<>();

            for (String name : materialNames) {
                if (name == null) continue;
                Material m = Material.matchMaterial(name);
                if (m != null) {
                    materials.add(m);
                } else {
                    Logger.error("Invalid material in config 'whitelisted-materials': " + name, 1, Factory.class);
                }
            }
            return List.copyOf(materials);
        }

        @Override
        public @NotNull PaperTileEntityConfig getFromConfig(ConfigurationNode config) {
            return new PaperTileEntityConfig(super.getFromConfig(config, getDefaults()), getMaterialList(config));
        }

        protected List<String> getBlockNameList() {
            return DEFAULT.getExemptedBlocks().stream().map(Material::name).toList();
        }

        private List<Material> getMaterialList(ConfigurationNode config) {
            List<String> materialNames = ConfigNodeUtil.getStringList(config, EXEMPTED_BLOCKS_PATH);

            ArrayList<Material> materials = new ArrayList<>();

            for (String name : materialNames) {
                if (name == null) continue;
                Material m = Material.matchMaterial(name);
                if (m != null) {
                    materials.add(m);
                } else {
                    Logger.error("Invalid material in config 'whitelisted-materials': " + name, 1, Factory.class);
                }
            }
            return List.copyOf(materials);
        }
        public static class FactoryProvider implements PlatformTileEntityConfig.Factory.FactoryProvider<Factory> {
            @Override
            public @NotNull Factory getFactory() {
                return new Factory();
            }
        }
    }
}
