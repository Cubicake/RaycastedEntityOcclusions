package games.cubi.raycastedAntiESP;

import games.cubi.raycastedAntiESP.config.DebugConfig;
import org.jetbrains.annotations.Range;

public class Logger {
    /*
    * Logging severity is from 0-10. If the configured severity is 0, no logs will be sent. If 1, only most important logs.
    *
    * Therefore, logs with level of 1 are most important and 10 least
    *
    * Note that by default the log levels are at 3, so any logs which should appear normally should be at 1-3. Additionally, loggers which fire every tick (or more often) should be at 10, and loggers firing frequently at 7-9
    * */

    private enum Level {
        INFO,
        WARN,
        ERROR
    }

    private static int getLevel(Level severity) {
        DebugConfig debug = RaycastedAntiESP.getConfigManager().getDebugConfig();
        return switch (severity) {
            case INFO -> debug.getInfoLevel();
            case WARN -> debug.getWarnLevel();
            case ERROR -> debug.getErrorLevel();
            default -> 1;
        };
    }

    public static void warning(Throwable throwable) {
        warning(processThrowable(throwable), 1);
    }
    public static void error(Throwable throwable) {
        error(processThrowable(throwable), 1);
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

    public static void info(String message, @Range(from = 1, to = 10) int level) {
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
                RaycastedAntiESP.logger().info(message);
            case WARN:
                RaycastedAntiESP.logger().warning(message);
            case ERROR:
                RaycastedAntiESP.logger().severe(message);
            default:
                RaycastedAntiESP.logger().severe( message + "| Additionally, severity " + severity + " is not supported by the logger.");
        }
    }
}
