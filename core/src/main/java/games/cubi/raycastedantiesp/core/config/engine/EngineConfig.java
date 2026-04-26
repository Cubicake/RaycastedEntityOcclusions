package games.cubi.raycastedantiesp.core.config.engine;

import games.cubi.raycastedantiesp.core.config.Config;
import games.cubi.raycastedantiesp.core.config.ConfigLoadException;
import games.cubi.raycastedantiesp.core.config.ConfigReader;
import org.spongepowered.configurate.ConfigurationNode;

public record EngineConfig(EngineMode mode, SimpleEngineConfig simpleConfig) implements Config {
    public static EngineConfig load(ConfigurationNode root) {
        ConfigurationNode node = ConfigReader.node(root, "engine");
        String modeName = ConfigReader.string(ConfigReader.node(node, "mode"), "engine.mode");
        EngineMode mode = EngineMode.fromString(modeName);
        if (mode == null) {
            throw new ConfigLoadException("engine.mode has unsupported value '" + modeName + "'");
        }
        return new EngineConfig(mode, SimpleEngineConfig.load(ConfigReader.node(node, "simple")));
    }

    public EngineMode getMode() {
        return mode;
    }
}
