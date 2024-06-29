package com.glodblock.github.ae2netanalyser.common.me.netdata;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum NodeFlag implements StringRepresentable {

    NORMAL, DENSE, MISSING;

    public static NodeFlag byIndex(int index) {
        return NodeFlag.values()[index];
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

}

