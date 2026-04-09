package games.cubi.logs;

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
