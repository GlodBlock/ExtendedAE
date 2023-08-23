package com.github.glodblock.epp.util;

public class CacheHolder<T> {

    private T obj;
    private boolean expired;

    private CacheHolder(T o) {
        this.obj = o;
        this.expired = false;
    }

    public static <T> CacheHolder<T> of(T obj) {
        return new CacheHolder<>(obj);
    }

    public void expired() {
        this.expired = true;
    }

    public T get() {
        return this.obj;
    }

    public boolean isValid() {
        return !this.expired;
    }

    public void update(T obj) {
        this.obj = obj;
        this.expired = false;
    }

}
