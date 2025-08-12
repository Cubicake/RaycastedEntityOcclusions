package games.cubi.raycastedEntityOcclusion;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


public class UpdateChecker {
    private final RaycastedEntityOcclusion plugin;

    public UpdateChecker(RaycastedEntityOcclusion plugin) {
        this.plugin = plugin;
        checkForUpdates(plugin, Bukkit.getConsoleSender());
    }


    public static CompletableFuture<String> fetchFeaturedVersion(RaycastedEntityOcclusion plugin) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            final String url = "https://api.modrinth.com/v2/project/bCjNZu0C/version?featured=true";
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
        fetchFeaturedVersion(plugin).thenAccept(version -> {
            // This runs synchronously when the version is fetched
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin.getDescription().getVersion().equals(version)) {
                    audience.sendRichMessage("<green>You are using the latest version of Raycasted Entity Occlusions.");
                } else {
                    audience.sendRichMessage("<red>You are not using the latest version of Raycasted Entity Occlusions. Please update to <green>v" + version+".");
                    if (audience instanceof Player) audience.sendRichMessage("\n" +
                            "<hover:show_text:'https://modrinth.com/project/bCjNZu0C/versions'><aqua><u><click:open_url:'https://modrinth.com/project/bCjNZu0C/versions'>Click here to download it.</click></u></aqua></hover>");
                }
            });
        }).exceptionally(ex -> {
            // Handle error (e.g., log the exception)
            Bukkit.getScheduler().runTask(plugin, () -> {
                Logger.warning("Failed to fetch version: " + ex.getMessage());
            });
            return null;
        });
    }
}
