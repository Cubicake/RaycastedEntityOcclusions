package games.cubi.raycastedantiesp.core.utils;

public interface Clearable {
    /**
     * Dereferences all internal references to other objects, allowing them to be garbage collected. In theory this isn't needed but idk what's causing the memory leak so we're covering all bases.
     */
    void clear();
}
