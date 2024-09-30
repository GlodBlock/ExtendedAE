package com.glodblock.github.extendedae.util;

import java.util.ArrayList;
import java.util.List;

public class InfinityCellInit {

    private static final List<Runnable> INIT = new ArrayList<>();
    private static final List<Runnable> INIT2 = new ArrayList<>();

    public static void add(Runnable task) {
        INIT.add(task);
    }

    public static void addModel(Runnable task) {
        INIT2.add(task);
    }

    public static void initModel() {
        INIT2.forEach(Runnable::run);
        INIT2.clear();
    }

    public static void init() {
        INIT.forEach(Runnable::run);
        INIT.clear();
    }

}
