package com.glodblock.github.extendedae.util;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;

/**
 * A least-recently-used cache backed by an arbitrary {@link Map}. Recency is tracked in a side
 * {@link LinkedHashSet} so the caller is free to pick whatever backing map fits the key type
 * (e.g. a fastutil or identity map).
 * <p>
 * This implementation is <b>not</b> thread-safe and must only be touched from a single thread.
 */
public class SingleThreadLRU<K, V> {

    private static final int DEFAULT_SIZE = 20;

    private final Map<K, V> backingMap;
    private final SequencedSet<K> accessOrder;
    private final int size;

    public SingleThreadLRU(Map<K, V> backingMap, int size) {
        this.backingMap = backingMap;
        this.accessOrder = new ObjectLinkedOpenHashSet<>(size);
        this.size = size;
    }

    public SingleThreadLRU(Map<K, V> backingMap) {
        this(backingMap, DEFAULT_SIZE);
    }

    public void put(K key, V value) {
        if (this.backingMap.size() >= this.size && !this.backingMap.containsKey(key)) {
            evictEldest();
        }
        this.backingMap.put(key, value);
        touch(key);
    }

    @Nullable
    public V get(K key) {
        var value = this.backingMap.get(key);
        if (value != null || this.backingMap.containsKey(key)) {
            touch(key);
        }
        return value;
    }

    public boolean containsKey(K key) {
        return this.backingMap.containsKey(key);
    }

    @Nullable
    public V remove(K key) {
        this.accessOrder.remove(key);
        return this.backingMap.remove(key);
    }

    public void clear() {
        this.accessOrder.clear();
        this.backingMap.clear();
    }

    public int size() {
        return this.backingMap.size();
    }

    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    private void touch(K key) {
        this.accessOrder.remove(key);
        this.accessOrder.add(key);
    }

    private void evictEldest() {
        var it = this.accessOrder.iterator();
        if (it.hasNext()) {
            var eldest = it.next();
            it.remove();
            this.backingMap.remove(eldest);
        }
    }

}
