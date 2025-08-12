package games.cubi.raycastedEntityOcclusion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Range;

public class Logger {
    private final static String PREFIX = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover><reset> ";
    private final static String PREFIX_WARNING = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover> <yellow><hover:show_text:'<yellow>Something went wrong'>[Warning]</hover> ";
    private final static String PREFIX_ERROR = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover> <red><hover:show_text:'<red>Something went wrong'>[Error]</hover> ";
    private static JavaPlugin plugin = null;

    private enum Level {
        INFO,
        WARN,
        ERROR
    }

    public static void info(String message) {
        forwardLog(/*PREFIX+*/message, Level.INFO);
    }

    public static void warning(String message) {
        forwardLog(/* PREFIX_WARNING + */ message, Level.WARN);
    }

    public static void error(String message) {
        forwardLog(/* PREFIX_ERROR+ */ message, Level.ERROR);
    }

    public static void debug(String message, @Range(from = 1, to = 10) int level) {
        int debugLevel = 2; // TODO: grab real debug level from config
        if (debugLevel <= level) {
            forwardLog(message, Level.INFO);
        }
    }

    private static void forwardLog(String message, Level severity) {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(Logger.class);
        }
        Component messageComponent = MiniMessage.miniMessage().deserialize(message);
        switch (severity) {
            case INFO:
                plugin.getComponentLogger().info(messageComponent);
                break;
            case WARN:
                plugin.getComponentLogger().warn(messageComponent);
                break;
            case ERROR:
                plugin.getComponentLogger().error(messageComponent);
                break;
            default:
                plugin.getComponentLogger().error("{} | Additionally, severity {} is not supported by the logger.", messageComponent, severity);
        }
        //Bukkit.getConsoleSender().sendRichMessage(message);
        //Bukkit.broadcast(MiniMessage.miniMessage().deserialize(message), "raycastedentityocclusions.receivelogmessages");
    }
}
