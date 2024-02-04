package com.glodblock.github.ae2netanalyser.common.me.network.netdata;

public enum FlagType {

    LINK, NODE;

    public static FlagType byIndex(int index) {
        return FlagType.values()[index];
    }

}
