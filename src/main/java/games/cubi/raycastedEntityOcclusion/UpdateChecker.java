package games.cubi.raycastedEntityOcclusion;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public class UpdateChecker {
    private final RaycastedEntityOcclusion plugin;

    public UpdateChecker(RaycastedEntityOcclusion plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Checking for updates...");
        checkForUpdates(plugin, Bukkit.getConsoleSender());
    }


    public static CompletableFuture<String> fetchFeaturedVersion(RaycastedEntityOcclusion plugin) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            final String url = "https://api.modrinth.com/v2/project/raycasted-entity-occlusions/version?featured=true";
            try (final InputStreamReader reader = new InputStreamReader(new URL(url).openConnection().getInputStream())) {
                final JsonArray array = new JsonArray();
                array.add(new BufferedReader(reader).readLine());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.size(); i++) {

                    sb.append(array.get(i).getAsString());
                }
                String apiData = sb.toString();
                JsonArray jsonArray = JsonParser.parseString(apiData).getAsJsonArray();
                JsonObject firstObject = jsonArray.get(0).getAsJsonObject();
                String versionNumber = firstObject.get("version_number").getAsString();

                future.complete(versionNumber);

            } catch (IOException e) {
                future.completeExceptionally(new IllegalStateException("Unable to fetch latest version", e));
            }
        });
        return future;
    }

    public static void checkForUpdates(RaycastedEntityOcclusion plugin, CommandSender audience) {
        //plugin.getLogger().info("Fetching latest version from Modrinth...");
        fetchFeaturedVersion(plugin).thenAccept(version -> {
            //plugin.getLogger().info("Latest version fetched: " + version);
            // This runs synchronously when the version is fetched
            Bukkit.getScheduler().runTask(plugin, () -> {
                //plugin.getLogger().info("Checking if current version matches the latest version...");
                if (plugin.getDescription().getVersion().equals(version)) {
                    audience.sendRichMessage("<green>You are using the latest version of Raycasted Entity Occlusions.");
                } else {
                    audience.sendRichMessage("<red>You are not using the latest version of Raycasted Entity Occlusions. Please update to <green>v" + version);
                }
            });
        }).exceptionally(ex -> {
            // Handle error (e.g., log the exception)
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getLogger().warning("Failed to fetch version: " + ex.getMessage());
            });
            return null;
        });
    }
}
