package games.cubi.raycastedantiesp.core.config;

import org.jetbrains.annotations.Nullable;

public enum BlockProcessorMode implements ConfigEnum {
    PACKETEVENTS("packetevents");

    private final String configName;

    BlockProcessorMode(String configName) {
        this.configName = configName;
    }

    public String getName() {
        return configName;
    }

    public static @Nullable BlockProcessorMode fromString(String name) {
        for (BlockProcessorMode mode : values()) {
            if (mode.configName.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public String[] getValues() {
        return new String[] {configName};
    }
}
