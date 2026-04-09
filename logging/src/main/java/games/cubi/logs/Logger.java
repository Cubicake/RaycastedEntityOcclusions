package games.cubi.logs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class Logger {
    private static PlatformLogger platformLogger = new FallbackLogger();

    private Logger() {}

    public static void init(PlatformLogger logger) {
        platformLogger = logger;
    }

    public static PlatformLogger get() {
        return platformLogger;
    }
    /**
     * Invoking this method will cause an error to be thrown.
     * @param throwable Used for the included stack trace. The message of the throwable will be used as the error message.
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) throws RuntimeException {
        platformLogger.warningAndReturn(throwable, level, source);
    }

    /**
     * @param throwable Used for the included stack trace. The message of the throwable will be used as the error message. It will not be thrown.
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
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
    /**
     * @param throwable Used for the included stack trace. The message of the throwable will be used as the error message.
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void error(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(throwable, level, source);
    }
    /**
     * @param message The message to log
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(message, throwable, level, source);
    }

    /**
     * Logs an error message including stack trace and serves as an early return by throwing an error. Nothing called after this method will be executed. Calling this is preferred over directly throwing errors, as it allows for error muting via configuration.
     * @param throwable The throwable to log, used for the included stack trace. The message of the throwable will be used as the error message.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     * @throws RuntimeException An exemption is always thrown. On some platforms this may be the provided throwable.
     * @throws CheckPreviousLogForError On platforms such as Paper,to prevent printing the stack trace twice.
     * **/
    public static void errorAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) throws RuntimeException {
        platformLogger.errorAndReturn(throwable, level, source);
    }

    /**
     * @param message The message to log
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void info(String message, @Range(from = 1, to = 10) int level, @NotNull Class<?>... source) {
        platformLogger.info(message, level, source);
    }
    /**
     * @param message The message to log
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void warning(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.warning(message, level, source);
    }
    /**
     * @param message The message to log
     * @param level The importance level of the message, from 1 to 10. This is used for filtering messages in the config, with 1 being the most important and 10 being the least important. For example, a message with level 3 will not be logged if the config is set to only log messages with level 2 or lower.
     * @param source For nested classes, the outer class should be first, and the innermost class last. May be left empty for obvious sources (init messages).
     */
    public static void error(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        platformLogger.error(message, level, source);
    }
}
