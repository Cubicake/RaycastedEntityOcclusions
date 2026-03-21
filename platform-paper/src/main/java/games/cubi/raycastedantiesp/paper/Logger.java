package games.cubi.raycastedantiesp.paper;

import games.cubi.logs.CheckPreviousLogForError;
import games.cubi.logs.PlatformLogger;
import org.jetbrains.annotations.Range;

public class Logger {

    static PlatformLogger loggerAdapter = null;

    public static void initialize(java.util.logging.Logger paperLogger) {
        loggerAdapter = new PaperLoggerAdapter(paperLogger);
    }

    public static PlatformLogger get() {
        return loggerAdapter;
    }

    /**
     * Logs a warning message and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the warning message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    public static void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        loggerAdapter.warningAndReturn(throwable, level);
    }

    public static void warning(Throwable throwable, @Range(from = 1, to = 10) int level) {
        loggerAdapter.warning(throwable, level);
    }

    /**
     * This should be used for testing only, and should be stripped out of production code. Logs a message at debug level, which is always visible
     * @param message The message to log
     *
     */
    public static void debug(String message) {
        loggerAdapter.debug(message);
    }

    public static void error(Throwable throwable, @Range(from = 1, to = 10) int level) {
        loggerAdapter.error(throwable, level);
    }

    public static void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level) {
        loggerAdapter.error(message, throwable, level);
    }

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    public static void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        loggerAdapter.errorAndReturn(throwable, level);
    }

    public static void info(String message, @Range(from = 1, to = 10) int level) {
        loggerAdapter.info(message, level);
    }

    public static void warning(String message, @Range(from = 1, to = 10) int level) {
        loggerAdapter.warning(message, level);
    }

    public static void error(String message, @Range(from = 1, to = 10) int level) {
        loggerAdapter.error(message, level);
    }
}
