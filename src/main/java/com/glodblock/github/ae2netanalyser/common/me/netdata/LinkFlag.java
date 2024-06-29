package com.glodblock.github.ae2netanalyser.common.me.netdata;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LinkFlag implements StringRepresentable {

    NORMAL, DENSE, COMPRESSED;

    public static LinkFlag byIndex(int index) {
        return LinkFlag.values()[index];
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

}
