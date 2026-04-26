package games.cubi.raycastedantiesp.core.config;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public final class ConfigReader {
    private ConfigReader() {}

    public static ConfigurationNode node(ConfigurationNode parent, String... path) {
        return parent.node((Object[]) path);
    }

    public static String string(ConfigurationNode node, String path) {
        Object raw = node.raw();
        if (raw instanceof String value) {
            return value;
        }
        throw invalid(path, "string", raw);
    }

    public static int integer(ConfigurationNode node, String path) {
        Object raw = node.raw();
        if (raw instanceof Number value && value.doubleValue() % 1 == 0) {
            return value.intValue();
        }
        throw invalid(path, "integer", raw);
    }

    public static boolean bool(ConfigurationNode node, String path) {
        Object raw = node.raw();
        if (raw instanceof Boolean value) {
            return value;
        }
        throw invalid(path, "boolean", raw);
    }

    public static List<String> stringList(ConfigurationNode node, String path) {
        if (node.virtual() || node.raw() == null) {
            return List.of();
        }
        if (node.childrenList().isEmpty()) {
            Object raw = node.raw();
            if (raw instanceof List<?> list && list.isEmpty()) {
                return List.of();
            }
            throw invalid(path, "list of strings", raw);
        }
        List<String> values = new ArrayList<>();
        for (ConfigurationNode child : node.childrenList()) {
            Object raw = child.raw();
            if (!(raw instanceof String value)) {
                throw invalid(path, "list of strings", raw);
            }
            values.add(value);
        }
        return List.copyOf(values);
    }

    public static List<Integer> integerList(ConfigurationNode node, String path) {
        if (node.virtual() || node.raw() == null) {
            return List.of();
        }
        if (node.childrenList().isEmpty()) {
            Object raw = node.raw();
            if (raw instanceof List<?> list && list.isEmpty()) {
                return List.of();
            }
            throw invalid(path, "list of integers", raw);
        }
        List<Integer> values = new ArrayList<>();
        for (ConfigurationNode child : node.childrenList()) {
            Object raw = child.raw();
            if (!(raw instanceof Number value) || value.doubleValue() % 1 != 0) {
                throw invalid(path, "list of integers", raw);
            }
            int id = value.intValue();
            if (id < 0) {
                throw new ConfigLoadException(path + " cannot contain negative block state IDs: " + id);
            }
            values.add(id);
        }
        return List.copyOf(values);
    }

    public static Object parseRawValue(String rawValue) {
        try {
            ConfigurationNode parsed = org.spongepowered.configurate.yaml.YamlConfigurationLoader.builder()
                    .source(() -> new java.io.BufferedReader(new java.io.StringReader("value: " + rawValue)))
                    .build()
                    .load();
            return parsed.node("value").raw();
        } catch (java.io.IOException e) {
            throw new ConfigLoadException("Invalid YAML value: " + rawValue, e);
        }
    }

    public static void setRaw(ConfigurationNode node, Object value) {
        try {
            node.set(value);
        } catch (SerializationException e) {
            throw new ConfigLoadException("Failed to set config value", e);
        }
    }

    private static ConfigLoadException invalid(String path, String expected, Object raw) {
        String actual = raw == null ? "null" : raw.getClass().getSimpleName();
        return new ConfigLoadException(path + " must be a " + expected + " but was " + actual);
    }
}
