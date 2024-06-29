package com.glodblock.github.ae2netanalyser.common.me.netdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum FlagType {

    LINK, NODE;

    public static FlagType byIndex(int index) {
        return FlagType.values()[index];
    }

    public static final Codec<Enum<?>> CODEC = Codec.INT.flatXmap(
            id -> {
                int type = id % 4;
                int ord = id >> 2;
                if (ord < 0) {
                    return DataResult.error(() -> "Invalid flag type.");
                }
                if (type == LINK.ordinal()) {
                    if (ord >= LinkFlag.values().length) {
                        return DataResult.error(() -> "Invalid flag type.");
                    } else {
                        return DataResult.success(LinkFlag.byIndex(ord));
                    }
                } else if (type == NODE.ordinal()) {
                    if (ord >= NodeFlag.values().length) {
                        return DataResult.error(() -> "Invalid flag type.");
                    } else {
                        return DataResult.success(NodeFlag.byIndex(ord));
                    }
                } else {
                    return DataResult.error(() -> "Invalid flag type.");
                }
            },
            e -> {
                if (e.getClass() == LinkFlag.class) {
                    return DataResult.success(e.ordinal() << 2 | LINK.ordinal());
                } else if (e.getClass() == NodeFlag.class) {
                    return DataResult.success(e.ordinal() << 2 | NODE.ordinal());
                } else {
                    return DataResult.error(() -> "Invalid flag type.");
                }
            }
    );

}
