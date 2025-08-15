package games.cubi.raycastedEntityOcclusion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Range;

public class Logger {
    private final static String PREFIX = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover><reset> ";
    private final static String PREFIX_WARNING = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover> <yellow><hover:show_text:'<yellow>Something went wrong'>[Warning]</hover> ";
    private final static String PREFIX_ERROR = "<hover:show_text:'Raycasted Entity Occlusions'><click:suggest_command:'/reo'><grey>[<gold>REO<grey>]</click></hover> <red><hover:show_text:'<red>Something went wrong'>[Error]</hover> ";

    private enum Level {
        INFO,
        WARN,
        ERROR
    }
    private static final int debugLevel = 1;
    private static final int errorLevel = 1;
    private static final int warningLevel = 1;

    private static int getLevel(Level severity) {
        return switch (severity) {
            case INFO -> debugLevel;
            case WARN -> warningLevel;
            case ERROR -> errorLevel;
            default -> 10;
        };
    }

    public static void info(String message) {
        forwardLog(message, Level.INFO, 1);
    }

    public static void warning(String message) {
        forwardLog(message, Level.WARN, 1);
    }

    public static void warning(Throwable throwable) {
        warning(processThrowable(throwable));
    }
    public static void error(Throwable throwable) {
        error(processThrowable(throwable));
    }

    private static String processThrowable(Throwable throwable) {
        StackTraceElement[] thrown = throwable.getStackTrace();
        StringBuilder message = new StringBuilder(throwable.getMessage() != null ? throwable.getMessage() : "No error message set");
        for (int i = 0; i < Math.min(3, thrown.length); i++) {
            StackTraceElement element = thrown[i];
            message.append(" at ").append(element.toString());

        }
        return message.toString();
    }

    public static void error(String message) {
        forwardLog(message, Level.ERROR, 1);
    }

    public static void debug(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.INFO, level);
    }

    public static void warning(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.WARN, level);
    }

    public static void error(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.ERROR, level);
    }

    private static void forwardLog(String message, Level severity, int level) {
        if (getLevel(severity) < level) {
            return;
        }

        switch (severity) {
            case INFO:
                RaycastedEntityOcclusion.logger().info(message);
            case WARN:
                RaycastedEntityOcclusion.logger().warning(message);
            case ERROR:
                RaycastedEntityOcclusion.logger().severe(message);
            default:
                RaycastedEntityOcclusion.logger().severe( message + "| Additionally, severity " + severity + " is not supported by the logger.");
        }
    }
}
