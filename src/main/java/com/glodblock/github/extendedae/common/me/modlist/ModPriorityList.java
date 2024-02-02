package com.glodblock.github.extendedae.common.me.modlist;

import appeng.api.stacks.AEKey;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;

import java.util.List;

public class ModPriorityList implements IPartitionList {

    private final String modid;

    public ModPriorityList(String modid) {
        this.modid = modid;
    }

    @Override
    public boolean isListed(AEKey input) {
        return this.modid.equals(input.getModId()) || this.modid.equals(Platform.getModName(input.getModId()));
    }

    @Override
    public boolean isEmpty() {
        return this.modid.isBlank();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }
}
