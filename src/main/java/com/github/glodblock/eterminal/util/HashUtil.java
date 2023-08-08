package com.github.glodblock.eterminal.util;

import it.unimi.dsi.fastutil.Hash;

import java.util.Objects;

public class HashUtil {

    public static final Hash.Strategy<Class<?>> CLASS = new Hash.Strategy<>() {
        @Override
        public int hashCode(Class<?> o) {
            return o.getName().hashCode();
        }

        @Override
        public boolean equals(Class<?> a, Class<?> b) {
            return a == b || (a != null && b != null && Objects.equals(a.getName(), b.getName()));
        }
    };

}
