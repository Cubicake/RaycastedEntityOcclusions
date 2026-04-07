package games.cubi.raycastedantiesp.core.utils;

import games.cubi.logs.Logger;
import org.jspecify.annotations.NullMarked;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// Not specific to RaycastedAntiESP, could be moved to a more general utils package if needed
@NullMarked
public record BuildProperties(String gitHashLong, String gitHashShort, String buildTime, Version version) {
    public static final BuildProperties CORE = fromResource("build-properties/core.yml");
    public static final BuildProperties PLATFORM = fromResource("build-properties/platform.yml");
    public static final BuildProperties LOGGER = fromResource("build-properties/logging.yml");
    public static final BuildProperties LOCATABLES = fromResource("build-properties/locatable-lib.yml");

    public static BuildProperties fromResource(String resourcePath) {
        try (InputStream inputStream = BuildProperties.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                Logger.error(new IllegalArgumentException("BuildProperties resource not found: " + resourcePath), 2, BuildProperties.class);
                return error();
            }

            String shortGit = null;
            String longGit = null;
            String buildTime = null;
            Version version = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) continue;

                    String[] parts = trimmedLine.split(":", 2);
                    if (parts.length != 2) continue;

                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    switch (key) {
                        case "short-git" -> shortGit = value;
                        case "long-git" -> longGit = value;
                        case "build-time" -> buildTime = value;
                        case "version" -> version = Version.parse(value);
                    }
                }
            }

            if (shortGit == null || longGit == null || buildTime == null || version == null) {
                Logger.error(new IllegalArgumentException("Missing required build properties in resource: " + resourcePath + ". Found shortGit=" + shortGit + ", longGit=" + longGit + ", buildTime=" + buildTime + ", version=" + version), 2, BuildProperties.class);
                return error();
            }
            return new BuildProperties(longGit, shortGit, buildTime, version);
        } catch (Exception e) {
            Logger.error(new IllegalArgumentException("Failed to parse BuildProperties resource: " + resourcePath, e), 2, BuildProperties.class);
            return error();
        }
    }

    private static BuildProperties error() {
        return new BuildProperties("unknown", "unknown", "unknown", new Version(-1, -1, -1, true));
    }

    record Version(short major, short minor, short patch, boolean snapshot) {
        Version(int major, int minor, int patch, boolean snapshot) {
            this((short) major, (short) minor, (short) patch, snapshot);
        }

        public static Version parse(String versionString) {
            String[] parts = versionString.split("[.-]");
            if (parts.length < 3) {
                Logger.errorAndReturn(new IllegalArgumentException("Version string must be in the format 'major.minor.patch'"), 2, BuildProperties.class, Version.class);
            }
            try {
                short major = Short.parseShort(parts[0]);
                short minor = Short.parseShort(parts[1]);
                short patch = Short.parseShort(parts[2]);
                boolean snapshot = versionString.contains("SNAPSHOT");
                return new Version(major, minor, patch, snapshot);
            } catch (NumberFormatException e) {
                Logger.errorAndReturn(new IllegalArgumentException("Version parts must be valid short integers. Attempted: " + versionString, e), 2, BuildProperties.class, Version.class);
                return new Version(-1, -1, -1, true);
            }
        }
    }
}
