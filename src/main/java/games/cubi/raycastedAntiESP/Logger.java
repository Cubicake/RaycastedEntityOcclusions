package games.cubi.raycastedAntiESP;

import games.cubi.raycastedAntiESP.config.DebugConfig;
import games.cubi.raycastedAntiESP.utils.CheckPreviousLogForError;
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
     * Note that by default the log levels are at 3, so any logs which should appear normally should be at 1-3. Additionally, loggers which fire every tick (or more often) should be at 10, and loggers firing frequently at 7-9
     * */
    private static final CheckPreviousLogForError earlyReturn = new CheckPreviousLogForError();

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
    public static void warningAndReturn(Throwable throwable) {
        warning(processThrowable(throwable), 1);
        throw earlyReturn;
    }

    public static void error(Throwable throwable) {
        error(processThrowable(throwable), 1);
    }

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/

    public static void errorAndReturn(Throwable throwable) {
        error(processThrowable(throwable), 1);
        throw earlyReturn;
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
        DebugConfig debug = RaycastedAntiESP.getConfigManager().getDebugConfig();

        if (debug.logToFile()) logToFile(message);

        if (getLevel(severity, debug) < level) {
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
                RaycastedAntiESP.logger().severe(message + "| Additionally, severity " + severity + " is not supported by the logger.");
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
                errorAndReturn(e);
            }
        } finally {
            flushLock.unlock();
        }
    }
}
