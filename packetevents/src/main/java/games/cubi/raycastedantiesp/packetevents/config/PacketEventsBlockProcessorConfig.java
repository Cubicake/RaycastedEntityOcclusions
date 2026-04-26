package games.cubi.raycastedantiesp.packetevents.config;

import games.cubi.raycastedantiesp.core.config.BlockProcessorConfig;
import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigExtension;
import games.cubi.raycastedantiesp.core.config.ConfigLoadException;
import games.cubi.raycastedantiesp.core.config.ConfigReader;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public record PacketEventsBlockProcessorConfig(List<Integer> tileEntityExemptedIds, List<Integer> tileEntityForceIncludedIds) implements Config {
    public static final ConfigExtension<PacketEventsBlockProcessorConfig> EXTENSION = new ConfigExtension<>() {
        @Override
        public Class<PacketEventsBlockProcessorConfig> type() {
            return PacketEventsBlockProcessorConfig.class;
        }

        @Override
        public PacketEventsBlockProcessorConfig load(ConfigurationNode config, BlockProcessorConfig blockProcessorConfig) {
            ConfigurationNode node = ConfigReader.node(config, "block-processor", "packetevents");
            PacketEventsBlockProcessorConfig packetEventsConfig = new PacketEventsBlockProcessorConfig(
                    ConfigReader.integerList(ConfigReader.node(node, "tile-entity-exempted-ids"), "block-processor.packetevents.tile-entity-exempted-ids"),
                    ConfigReader.integerList(ConfigReader.node(node, "tile-entity-force-included-ids"), "block-processor.packetevents.tile-entity-force-included-ids")
            );
            if (!blockProcessorConfig.trackAllBlocks() && !packetEventsConfig.tileEntityForceIncludedIds().isEmpty()) {
                throw new ConfigLoadException("block-processor.packetevents.tile-entity-force-included-ids must be empty when block-processor.track-all-blocks is false");
            }
            return packetEventsConfig;
        }
    };
}
