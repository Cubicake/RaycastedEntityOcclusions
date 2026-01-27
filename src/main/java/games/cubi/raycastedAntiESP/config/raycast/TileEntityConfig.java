package games.cubi.raycastedAntiESP.config.raycast;

import games.cubi.raycastedAntiESP.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileEntityConfig extends RaycastConfig {
    private static final String PATH = "checks.tile-entity";
    private static final List<Material> DEFAULT_EXEMPTED_BLOCKS = List.of(Material.BEACON);
    private final List<Material> exemptedBlocks; // This list is immutable

    public static final TileEntityConfig DEFAULT = new TileEntityConfig(3, 1, 48, 10, true, DEFAULT_EXEMPTED_BLOCKS);

    public TileEntityConfig(byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public TileEntityConfig(int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public TileEntityConfig(boolean enabled, List<Material> exemptedBlocks) {
        super(enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public TileEntityConfig(RaycastConfig superConfig,List<Material> exemptedBlocks) {
        super(superConfig);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    @Override
    public int getVisibleRecheckIntervalSeconds() {
        return super.getVisibleRecheckInterval();
    }

    public List<Material> getExemptedBlocks() {
        return exemptedBlocks; // Immutable list
    }

    private static List<Material> getMaterialList(FileConfiguration config) {
        List<String> materialNames = config.getStringList(Factory.EXEMPTED_BLOCKS_PATH);

        ArrayList<Material> materials = new ArrayList<>();

        for (String name : materialNames) {
            if (name == null) continue;
            Material m = Material.matchMaterial(name);
            if (m != null) {
                materials.add(m);
            } else {
                Logger.error("Invalid material in config 'whitelisted-materials': " + name,1);
            }
        }
        return List.copyOf(materials);
    }

    public static class Factory extends RaycastConfig.Factory {
        private static final String EXEMPTED_BLOCKS_PATH = PATH + ".exempted-blocks";
        public Factory() {
            super(PATH);
        }

        @Override
        public @NotNull TileEntityConfig getFromConfig(FileConfiguration config, @Nullable RaycastConfig defaults) {
            TileEntityConfig fallback = defaults instanceof TileEntityConfig tileDefaults ? tileDefaults : DEFAULT;
            return new TileEntityConfig(super.getFromConfig(config, fallback), getMaterialList(config));
        }

        @Override
        public @NotNull RaycastConfig.Factory setDefaults(FileConfiguration config, @Nullable RaycastConfig defaults) {
            TileEntityConfig fallback = defaults instanceof TileEntityConfig tileDefaults ? tileDefaults : DEFAULT;
            super.setDefaults(config, fallback);
            config.addDefault(EXEMPTED_BLOCKS_PATH, fallback.getExemptedBlocks().stream().map(Material::name).toList());
            return this;
        }
    }
}
