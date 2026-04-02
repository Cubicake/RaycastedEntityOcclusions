package games.cubi.logs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class Logger {
    private static PlatformLogger platformLogger;

    private Logger() {}

    public static void init(PlatformLogger logger) {
        platformLogger = logger;
    }

    public static PlatformLogger get() {
        return platformLogger;
    }

    public static void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.warningAndReturn(throwable, level, source);
    }

    public static void warning(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.warning(throwable, level, source);
    }

    /**
     * This should be used for testing only, and should be stripped out of production code. Logs a message at debug level, which is always visible
     * @param message The message to log
     *
     */
    public static void debug(String message) {
        platformLogger.debug(message);
    }

    public static void error(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(throwable, level, source);
    }

    public static void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(message, throwable, level, source);
    }

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    public static  void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.errorAndReturn(throwable, level, source);
    }

    public static void info(String message, @Range(from = 1, to = 10) int level, @NotNull Class<?>... source) {
        platformLogger.info(message, level, source);
    }

    public static void warning(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.warning(message, level, source);
    }

    public static void error(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(message, level, source);
    }
}
