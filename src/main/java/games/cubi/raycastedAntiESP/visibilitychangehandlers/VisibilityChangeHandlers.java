package games.cubi.raycastedAntiESP.visibilitychangehandlers;

public class VisibilityChangeHandlers {
    private static VisibilityChangeHandlers instance;

    private VisibilityChangeHandlers() {}

    public static VisibilityChangeHandlers get() {
        if (instance == null) {
            instance = new VisibilityChangeHandlers();
        }
        return instance;
    }
}
