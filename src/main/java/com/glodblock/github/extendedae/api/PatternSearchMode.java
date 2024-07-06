package com.glodblock.github.extendedae.api;

public enum PatternSearchMode {

    IN,
    OUT,
    IN_OUT;

    public boolean isOut() {
        return this != IN;
    }

    public boolean isIn() {
        return this != OUT;
    }

}
