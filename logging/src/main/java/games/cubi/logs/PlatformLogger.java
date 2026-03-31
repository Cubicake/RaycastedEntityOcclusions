package games.cubi.logs;

import org.jetbrains.annotations.Range;

/**
 * Logging severity is from 0-10. If the configured severity is 0, no logs will be sent. If 1, only most important logs.
 * <p>
 * Therefore, logs with level of 1 are most important and 10 least
 * <p>
 * Note that by default the log levels are at 5, so any logs which should appear normally should be at 1-5. Additionally, loggers which fire several times a tick should be at 10, once a tick at 9, and loggers firing frequently at 6-8
 * */
public interface PlatformLogger {
    /*
     * Logging severity is from 0-10. If the configured severity is 0, no logs will be sent. If 1, only most important logs.
     *
     * Therefore, logs with level of 1 are most important and 10 least
     *
     * Note that by default the log levels are at 5, so any logs which should appear normally should be at 1-5. Additionally, loggers which fire several times a tick should be at 10, once a tick at 9, and loggers firing frequently at 6-8
     * */
    CheckPreviousLogForError earlyReturn = new CheckPreviousLogForError();

    enum Level {
        INFO,
        WARN,
        ERROR
    }

    /**
     * Logs a warning message and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the warning message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?> source);

    void warning(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?> source);

    /**
     * This should be used for testing only, and should be stripped out of production code. Logs a message at debug level, which is always visible
      * @param message The message to log
       *
     */
    void debug(String message);

    void error(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?> source);

    void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level, Class<?> source);

    /**
     * Logs an error message including stack trace and serves as an early return. Nothing called after this method will be executed.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message
     * @throws CheckPreviousLogForError Always throws this to allow for early return from functions after logging an error
     * **/
    default void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?> source) {
        error(throwable, level, source);
        throw earlyReturn;
    }

    void info(String message, @Range(from = 1, to = 10) int level, Class<?> source);
    void warning(String message, @Range(from = 1, to = 10) int level, Class<?> source);
    void error(String message, @Range(from = 1, to = 10) int level, Class<?> source);
}
