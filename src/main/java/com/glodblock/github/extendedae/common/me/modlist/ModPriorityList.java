package com.glodblock.github.extendedae.common.me.modlist;

import appeng.api.stacks.AEKey;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.util.FCUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.*;

public class ModPriorityList implements IPartitionList {

    private final ObjectSet<String> modids = new ObjectOpenHashSet<>();

    public ModPriorityList(String modid) {
        Collections.addAll(this.modids, FCUtil.trimSplit(modid));
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
