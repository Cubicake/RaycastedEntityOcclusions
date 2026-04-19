package games.cubi.raycastedantiesp.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentSelfMap<T> implements CanonicalSet<T> {
    private final ConcurrentHashMap<T, T> backingMap = new ConcurrentHashMap<>();

    public ConcurrentSelfMap() {}

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public boolean contains(T keyValue) {
        return backingMap.containsKey(keyValue);
    }

    @Override
    public @Nullable T get(T keyValue) {
        return backingMap.get(keyValue);
    }

    @Override
    public @Nullable T add(T keyValue) {
        return backingMap.put(keyValue, keyValue);
    }

    @Override
    public void addAll(Set<? extends T> keyValues) {
        for (T keyValue : keyValues) {
            backingMap.put(keyValue, keyValue);
        }
    }

    @Override
    public @Nullable T remove(T keyValue) {
        return backingMap.remove(keyValue);
    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    @Override
    public @NotNull Set<T> keySet() {
        return backingMap.keySet();
    }

    @Override
    public @NotNull Collection<T> values() {
        return backingMap.keySet();
    }
}
