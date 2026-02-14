package games.cubi.raycastedAntiESP.config;

import games.cubi.raycastedAntiESP.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TileEntityConfig extends RaycastConfig {
    private static final String PATH = "checks.tile-entity";
    private final List<Material> exemptedBlocks; // This list is immutable

    public TileEntityConfig(byte engineMode, byte maxOccludingCount, short alwaysShowRadius, short raycastRadius, short visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
        this.exemptedBlocks = List.copyOf(exemptedBlocks);
    }

    public TileEntityConfig(int engineMode, int maxOccludingCount, int alwaysShowRadius, int raycastRadius, int visibleRecheckInterval, boolean enabled, List<Material> exemptedBlocks) {
        super(engineMode, maxOccludingCount, alwaysShowRadius, raycastRadius, visibleRecheckInterval, enabled);
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
        return super.getVisibleRecheckIntervalRaw();
    }

    @Override
    public short getVisibleRecheckIntervalTicks() {
        return (short) (super.getVisibleRecheckIntervalRaw() * 20);
    }

    public List<Material> getExemptedBlocks() {
        return exemptedBlocks; // Immutable list
    }

    public static TileEntityConfig getFromConfig(FileConfiguration config, TileEntityConfig defaults) {
        return new TileEntityConfig(RaycastConfig.getFromConfig(config, PATH, defaults), getMaterialList(config));
    }

    private static List<Material> getMaterialList(FileConfiguration config) {
        List<String> materialNames = config.getStringList(PATH);

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

    public static void setDefaults(FileConfiguration config, TileEntityConfig defaults) {
        RaycastConfig.setDefaults(config, PATH, defaults);
        config.addDefault(PATH+".exempted-blocks", defaults.getExemptedBlocks().stream().map(Material::name).toList());
    }
}