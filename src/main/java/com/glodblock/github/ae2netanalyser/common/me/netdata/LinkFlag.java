package com.glodblock.github.ae2netanalyser.common.me.netdata;

public enum LinkFlag {

    NORMAL, DENSE, COMPRESSED;

    public static LinkFlag byIndex(int index) {
        return LinkFlag.values()[index];
    }

}
