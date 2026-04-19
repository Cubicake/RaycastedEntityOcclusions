package games.cubi.raycastedantiesp.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * A Set-like data structure that stores canonical instances of key values. Designed for use with locatables as all locatables exhibit the same `equals` and `hashcode` behavior regardless of their mutability or extra data, so this allows for storing a single canonical instance of each locatable and retrieving it based on any key value that is equal to it.
 * @param <T>
 */
public interface CanonicalSet<T> {
    int size();

    boolean isEmpty();

    boolean contains(T keyValue);

    @Nullable T get(T keyValue);

    @Nullable T add(T keyValue);

    void addAll(Set<? extends T> keyValues);

    @Nullable T remove(T keyValue);

    void clear();

    @NotNull Set<T> keySet();

    @NotNull Collection<T> values();
}
