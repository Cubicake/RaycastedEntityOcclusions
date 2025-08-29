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

            final String url = "https://api.modrinth.com/v2/project/bCjNZu0C/version?featured=true";

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

    public static void checkForUpdates(RaycastedAntiESP plugin, CommandSender audience) {
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
            Bukkit.getScheduler().runTask(plugin, () -> {
                Logger.error(ex);
            });
            return null;
        });
    }
}
