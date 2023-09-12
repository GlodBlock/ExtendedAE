package com.github.glodblock.epp.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public class AERotatableBlocks {

    private final static Collection<String> LIST = ImmutableList.of(
            "ex_drive"
    );

    public static boolean check(ResourceLocation rl) {
        for (var l : LIST) {
            if (rl.toString().contains(l)) {
                return true;
            }
        }
        return false;
    }

}
