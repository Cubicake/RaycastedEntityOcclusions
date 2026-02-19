package games.cubi.raycastedAntiESP.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public final class ConfigNodeUtil {
    private ConfigNodeUtil() {}

    public static @NotNull ConfigurationNode node(@NotNull ConfigurationNode config, @NotNull String path) {
        // REO config keys are dot-delimited and do not contain literal dots in key names
        return config.node((Object[]) path.split("\\."));
    }

    public static int getInt(@NotNull ConfigurationNode config, @NotNull String path, int defaultValue) {
        return node(config, path).getInt(defaultValue);
    }

    public static boolean getBoolean(@NotNull ConfigurationNode config, @NotNull String path, boolean defaultValue) {
        return node(config, path).getBoolean(defaultValue);
    }

    public static String getString(@NotNull ConfigurationNode config, @NotNull String path, String defaultValue) {
        return node(config, path).getString(defaultValue);
    }

    public static @NotNull List<String> getStringList(@NotNull ConfigurationNode config, @NotNull String path) {
        try {
            return new ArrayList<>(node(config, path).getList(String.class, List.of()));
        } catch (SerializationException ignored) {
            return new ArrayList<>();
        }
    }

    public static void addDefault(@NotNull ConfigurationNode config, @NotNull String path, Object value) {
        ConfigurationNode target = node(config, path);
        if (!target.virtual()) return;
        setNodeValue(target, value);
    }

    public static boolean contains(@NotNull ConfigurationNode config, @NotNull String path) {
        return !node(config, path).virtual();
    }

    public static Object get(@NotNull ConfigurationNode config, @NotNull String path) {
        return node(config, path).raw();
    }

    public static void set(@NotNull ConfigurationNode config, @NotNull String path, Object value) {
        setNodeValue(node(config, path), value);
    }

    private static void setNodeValue(ConfigurationNode node, Object value) {
        try {
            if (value instanceof List<?> list) {
                node.set(new ArrayList<>(list));
            } else {
                node.set(value);
            }
        } catch (SerializationException e) {
            throw new RuntimeException("Failed to write config value", e);
        }
    }
}
