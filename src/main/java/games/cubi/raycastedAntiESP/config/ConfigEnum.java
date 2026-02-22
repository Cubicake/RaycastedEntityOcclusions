package games.cubi.raycastedAntiESP.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ConfigEnum {
    String[] getValues();

    Registry REGISTRY = new Registry();

    default void register() {
        REGISTRY.add(this.getClass());
    }

    static String[] getAllValues() {
        return REGISTRY.getAllValues();
    }

    final class Registry {
        private final Set<Class<? extends ConfigEnum>> enums = new HashSet<>();

        void add(Class<? extends ConfigEnum> e) {
            enums.add(e);
        }

        String[] getAllValues() {
            return enums.stream()
                .flatMap(e -> Arrays.stream(e.getEnumConstants()[0].getValues()))
                .toArray(String[]::new);
        }
    }
}
