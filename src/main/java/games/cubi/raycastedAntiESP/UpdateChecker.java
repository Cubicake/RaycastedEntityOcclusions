package games.cubi.raycastedAntiESP;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;


public class UpdateChecker {
    private static CompletableFuture<String> fetchFeaturedVersion(RaycastedAntiESP plugin) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final String url = "https://api.modrinth.com/v2/project/bCjNZu0C/version";

            try (final InputStreamReader reader = new InputStreamReader(new URI(url).toURL().openConnection().getInputStream())) {
                final JsonArray array = new JsonArray();
                array.add(new BufferedReader(reader).readLine());
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < array.size(); i++) {
                    stringBuilder.append(array.get(i).getAsString());
                }
                String apiData = stringBuilder.toString();
                JsonArray jsonArray = JsonParser.parseString(apiData).getAsJsonArray();
                JsonObject firstObject = jsonArray.get(0).getAsJsonObject();
                String versionNumber = firstObject.get("version_number").getAsString();

                future.complete(versionNumber);

            } catch (IOException | URISyntaxException e) {
                future.completeExceptionally(new IllegalStateException("Unable to fetch latest version", e));
            }
        });
        return future;
    }

    private static final int INVALID_VERSION_FORMAT = -2; private static final int CURRENT_IS_OLDER = -1; private static final int VERSIONS_ARE_EQUAL = 0; private static final int CURRENT_IS_NEWER = 1;

    private static int checkIfLaterThan(String currentVersion, String fetchedVersion) {
        String[] currentParts = currentVersion.replace("v", "").split("[.-]");
        String[] fetchedParts = fetchedVersion.replace("v", "").split("[.-]");

        if (currentParts.length < 3 || fetchedParts.length < 3) {
            return INVALID_VERSION_FORMAT;
        }

        int majorVersionDifference = Integer.parseInt(currentParts[0]) - Integer.parseInt(fetchedParts[0]);

        if (majorVersionDifference == VERSIONS_ARE_EQUAL) {
            int minorVersionDifference = Integer.parseInt(currentParts[1]) - Integer.parseInt(fetchedParts[1]);

            if (minorVersionDifference == VERSIONS_ARE_EQUAL) {
                int patchVersionDifference = Integer.parseInt(currentParts[2]) - Integer.parseInt(fetchedParts[2]);
                return patchVersionDifference;
            }
            return minorVersionDifference;
        }
        return majorVersionDifference;
    }

    public static void checkForUpdates(RaycastedAntiESP plugin, CommandSender audience) {
        fetchFeaturedVersion(plugin).thenAccept(version -> {
            // This runs synchronously when the version is fetched
            Bukkit.getScheduler().runTask(plugin, () -> {
                int versionCheck = checkIfLaterThan(plugin.getDescription().getVersion(), version);

                if (versionCheck == VERSIONS_ARE_EQUAL) {
                    audience.sendRichMessage("<green>You are using the latest version of Raycasted Entity Occlusions.");
                }

                if (versionCheck == CURRENT_IS_OLDER) {
                    audience.sendRichMessage("<red>You are not using the latest version of Raycasted Entity Occlusions. Please update to <green>v" + version+".");
                    if (audience instanceof Player) audience.sendRichMessage("\n" + "<hover:show_text:'https://modrinth.com/project/bCjNZu0C/versions'><aqua><u><click:open_url:'https://modrinth.com/project/bCjNZu0C/versions'>Click here to download it.</click></u></aqua></hover>");
                }

                if (versionCheck == CURRENT_IS_NEWER) {
                    audience.sendRichMessage("<yellow>You are using a development build of Raycasted Entity Occlusions. The latest stable version is <green>v" + version + "<yellow>.");
                }

                if (versionCheck == INVALID_VERSION_FORMAT) {
                    audience.sendRichMessage("<red>Unable to check for updates, invalid version format.");
                }
            });
        }).exceptionally(ex -> {
            Logger.error("An error occured while checking for plugin updates", ex, 4);
            return null;
        });
    }
}
