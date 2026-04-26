package games.cubi.raycastedantiesp.core.config;

import org.spongepowered.configurate.ConfigurationNode;

public record BlockProcessorConfig(BlockProcessorMode mode, boolean trackAllBlocks) implements Config {
    public static BlockProcessorConfig load(ConfigurationNode root) {
        ConfigurationNode node = ConfigReader.node(root, "block-processor");
        String modeName = ConfigReader.string(ConfigReader.node(node, "mode"), "block-processor.mode");
        BlockProcessorMode mode = BlockProcessorMode.fromString(modeName);
        if (mode == null) {
            throw new ConfigLoadException("block-processor.mode has unsupported value '" + modeName + "'");
        }
        return new BlockProcessorConfig(
                mode,
                ConfigReader.bool(ConfigReader.node(node, "track-all-blocks"), "block-processor.track-all-blocks")
        );
    }
}
