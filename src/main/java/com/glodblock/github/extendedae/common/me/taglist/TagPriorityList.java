package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class TagPriorityList implements IPartitionList {

    private final Set<TagKey<?>> whiteSet;
    private final Set<TagKey<?>> blackSet;
    private final boolean emptyWhiteExp;
    private final boolean emptyBlackExp;
    // Cache isn't fast enough here, so I have to use map here.
    private final Reference2BooleanMap<Object> memory = new Reference2BooleanOpenHashMap<>();

    public TagPriorityList(Set<TagKey<?>> whiteKeys, Set<TagKey<?>> blackKeys, boolean emptyWhiteExp, boolean emptyBlackExp) {
        this.whiteSet = whiteKeys;
        this.blackSet = blackKeys;
        this.emptyWhiteExp = emptyWhiteExp;
        this.emptyBlackExp = emptyBlackExp;
    }

    @Override
    public boolean isListed(AEKey input) {
        Object key = input.getPrimaryKey();
        return this.memory.computeIfAbsent(key, this::eval);
    }

    @Override
    public boolean isEmpty() {
        return this.emptyWhiteExp && this.emptyBlackExp;
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }

    @SuppressWarnings("deprecation")
    private boolean eval(@NotNull Object obj) {
        Holder<?> refer = null;
        if (obj instanceof Item item) {
            refer = item.builtInRegistryHolder();
        } else if (obj instanceof Fluid fluid) {
            refer = fluid.builtInRegistryHolder();
        }
        if (refer != null) {
            boolean pass = true;

            if (!this.emptyWhiteExp) {
                pass = refer.tags().anyMatch(this.whiteSet::contains);
            }

            if (pass) {
                if (!this.blackSet.isEmpty()) {
                    return refer.tags().noneMatch(this.blackSet::contains);
                }
                return true;
            }
        }
        return false;
    }

}
