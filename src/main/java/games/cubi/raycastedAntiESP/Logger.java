package games.cubi.raycastedAntiESP;

import games.cubi.raycastedAntiESP.config.ConfigManager;
import games.cubi.raycastedAntiESP.config.DebugConfig;
import games.cubi.raycastedAntiESP.utils.CheckPreviousLogForError;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {
    /*
     * Logging severity is from 0-10. If the configured severity is 0, no logs will be sent. If 1, only most important logs.
     *
     * Therefore, logs with level of 1 are most important and 10 least
     *
     * Note that by default the log levels are at 5, so any logs which should appear normally should be at 1-5. Additionally, loggers which fire several times a tick should be at 10, once a tick at 9, and loggers firing frequently at 6-8
     * */
    private static final CheckPreviousLogForError earlyReturn = new CheckPreviousLogForError();

    public enum Frequency {
        ONCE_PER_TICK(9),
        MULTI_PER_TICK(10),
        CONFIG_LOAD(3), //flawed premise until config loads before
        /**For use where if the error logger is reached, something has gone catastrophically wrong and the plugin likely cannot function properly. This should be used very sparingly, and only for the most severe errors, as it will be logged even at the lowest log level settings.**/
        CRITICAL(1),
        ;
        public final int value;

        Frequency(int i) {
            this.value = i;
        }
    }

    private enum Level {
        INFO,
        WARN,
        ERROR
    }

    private static int getLevel(Level severity, DebugConfig debug) {
        return switch (severity) {
            case INFO -> debug.getInfoLevel();
            case WARN -> debug.getWarnLevel();
            case ERROR -> debug.getErrorLevel();
            default -> 1;
        };
    }
    /**
     * Logs a warning message and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the warning message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    public static void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        warning(processThrowable(throwable), level);
        throw earlyReturn;
    }

    public static void warning(Throwable throwable, @Range(from = 1, to = 10) int level) {
        warning(processThrowable(throwable), level);
    }

    @Deprecated
    public static void debug(String message) {
        forwardLog(message, Level.INFO, 1);
    }

    public static void error(Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable), level);
    }

    public static void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable, message), level);
    }

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/

    public static void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable), level);
        throw earlyReturn;
    }

    private static String processThrowable(Throwable throwable) {
        return processThrowable(throwable, null);
    }

    private static String processThrowable(Throwable throwable, @Nullable String errorMessage) {
        StackTraceElement[] thrown = throwable.getStackTrace();
        StringBuilder message = new StringBuilder();
        if (errorMessage != null) {
            message.append(errorMessage);
        } else {
            message.append(throwable.getMessage() != null ? throwable.getMessage() : "An error occurred but no error message was set |");
        }
        for (int i = 0; i < Math.min(4, thrown.length); i++) {
            StackTraceElement element = thrown[i];
            message.append("\n at ").append(element.toString());

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
        ConfigManager configManager = RaycastedAntiESP.getConfigManager();
        if (configManager != null && configManager.getDebugConfig() != null) {
            DebugConfig debug = configManager.getDebugConfig();

            if (debug.logToFile()) logToFile(message);

            if (getLevel(severity, debug) < level) {
                return;
            }
        }

        switch (severity) {
            case INFO:
                RaycastedAntiESP.logger().info(message);
                break;
            case WARN:
                RaycastedAntiESP.logger().warning(message);
                break;
            case ERROR:
                RaycastedAntiESP.logger().severe(message);
                break;
            default:
                RaycastedAntiESP.logger().severe(message + "| Additionally, severity " + severity + " is not supported by the logger.");
                break;
        }
    }

    private static final AtomicReference<ConcurrentLinkedQueue<String>> logBuffer = new AtomicReference<>(new ConcurrentLinkedQueue<String>());
    private static final String logName = System.currentTimeMillis()+""; // Unique and new logs will have larger numbers
    private static final ReentrantLock flushLock = new ReentrantLock();

    private static void logToFile(String line) { //todo broken
        if (line == null) return;
        logBuffer.get().offer(line);
    }

    public static void flush() {
        flushLock.lock();
        try {
            // Atomically swap buffers so producers can keep logging without blocking on I/O.
            ConcurrentLinkedQueue<String> toWrite = logBuffer.getAndSet(new ConcurrentLinkedQueue<>());

            if (toWrite.isEmpty()) return;

            try (BufferedWriter w = Files.newBufferedWriter(
                    RaycastedAntiESP.get().getDataFolder().toPath().resolve("logs").resolve( logName +".log"),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND)) {

                String s;
                while ((s = toWrite.poll()) != null) {
                    w.write(s);
                    w.newLine(); // ensure each entry is on its own line
                }
            } catch (IOException e) {
                error("An error occured while attempting to flush logs", e, 2);
            }
        } finally {
            flushLock.unlock();
        }
    }
}
