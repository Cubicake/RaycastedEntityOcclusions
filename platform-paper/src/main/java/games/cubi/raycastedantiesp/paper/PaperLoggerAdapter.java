package games.cubi.raycastedantiesp.paper;

import games.cubi.logs.CheckPreviousLogForError;
import games.cubi.logs.PlatformLogger;
import games.cubi.raycastedantiesp.paper.config.ConfigManager;
import games.cubi.raycastedantiesp.paper.config.DebugConfig;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class PaperLoggerAdapter implements PlatformLogger {

    private final java.util.logging.Logger logger;

    public PaperLoggerAdapter(java.util.logging.Logger logger) {
        this.logger = logger;
    }
    /*
     * Logging severity is from 0-10. If the configured severity is 0, no logs will be sent. If 1, only most important logs.
     *
     * Therefore, logs with level of 1 are most important and 10 least
     *
     * Note that by default the log levels are at 5, so any logs which should appear normally should be at 1-5. Additionally, loggers which fire several times a tick should be at 10, once a tick at 9, and loggers firing frequently at 6-8
     * */

    private int getLevel(Level severity, DebugConfig debug) {
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
    @Override
    public void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        warning(processThrowable(throwable), level);
        throw earlyReturn;
    }

    @Override
    public void warning(Throwable throwable, @Range(from = 1, to = 10) int level) {
        warning(processThrowable(throwable), level);
    }

    @Deprecated @Override
    public void debug(String message) {
        forwardLog(message, Level.INFO, 1);
    }

    @Override
    public void error(Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable), level);
    }

    @Override
    public void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable, message), level);
    }

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    @Override
    public void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level) {
        error(processThrowable(throwable), level);
        throw earlyReturn;
    }

    private String processThrowable(Throwable throwable) {
        return processThrowable(throwable, null);
    }

    private String processThrowable(Throwable throwable, @Nullable String errorMessage) {
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

    @Override
    public void info(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.INFO, level);
    }

    @Override
    public void warning(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.WARN, level);
    }

    @Override
    public void error(String message, @Range(from = 1, to = 10) int level) {
        forwardLog(message, Level.ERROR, level);
    }

    private void forwardLog(String message, Level severity, int level) {
        ConfigManager configManager = RaycastedAntiESP.getConfigManager();
        if (configManager != null && configManager.getDebugConfig() != null) {
            DebugConfig debug = configManager.getDebugConfig();

            if (getLevel(severity, debug) < level) {
                return;
            }
        }

        switch (severity) {
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warning(message);
                break;
            case ERROR:
                logger.severe(message);
                break;
            default:
                logger.severe(message + "| Additionally, severity " + severity + " is not supported by the logger.");
                break;
        }
    }
}
