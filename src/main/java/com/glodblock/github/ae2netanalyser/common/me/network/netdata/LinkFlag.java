package com.glodblock.github.ae2netanalyser.common.me.network.netdata;

public enum LinkFlag {

    NORMAL, DENSE, COMPRESSED;

    public static LinkFlag byIndex(int index) {
        return LinkFlag.values()[index];
    }

}
