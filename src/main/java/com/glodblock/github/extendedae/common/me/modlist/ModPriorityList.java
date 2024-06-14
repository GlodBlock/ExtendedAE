package com.glodblock.github.extendedae.common.me.modlist;

import appeng.api.stacks.AEKey;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;

import java.util.*;

public class ModPriorityList implements IPartitionList {

    private final Set<String> modids = new HashSet<>();

    public ModPriorityList(String modid) {
        Collections.addAll(this.modids, modid.split(","));
    }

    @Override
    public boolean isListed(AEKey input) {
        return this.modids.contains(input.getModId()) || this.modids.contains(Platform.getModName(input.getModId()));
    }

    @Override
    public boolean isEmpty() {
        return this.modids.isEmpty();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }
}
