package games.cubi.logs;

import org.jetbrains.annotations.Range;

public class FallbackLogger implements PlatformLogger {
    @Override
    public void warningAndReturn(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
        } else {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public void warning(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        warning(PlatformLogger.processThrowable(throwable), level, source);
    }

    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }

    @Override
    public void error(Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        error(PlatformLogger.processThrowable(throwable), level, source);
    }

    @Override
    public void error(String message, Throwable throwable, @Range(from = 1, to = 10) int level, Class<?>... source) {
        error(PlatformLogger.processThrowable(throwable, message), level, source);
    }

    @Override
    public void info(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        System.out.println("[INFO] " + constructMessage(message, source));
    }

    @Override
    public void warning(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        System.out.println("[WARN] " + constructMessage(message, source));
    }

    @Override
    public void error(String message, @Range(from = 1, to = 10) int level, Class<?>... source) {
        System.err.println("[ERROR] " + constructMessage(message, source));
    }

    private String constructMessage(String message, Class<?>... source) {
        Class<?>[] combined = new Class<?>[source.length + 1];
        combined[0] = CubiGames.class; // Really stupid basic way to sign the log as by us. This logger should never be used anyways, so it's fine
        System.arraycopy(source, 0, combined, 1, source.length);
        return PlatformLogger.constructMessage(message, combined) + " | Additionally, no logger was found, so this message was printed using the fallback logger.";
    }

    static class CubiGames {}
}
