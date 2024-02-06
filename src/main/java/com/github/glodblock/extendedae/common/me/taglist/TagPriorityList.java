package com.github.glodblock.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class TagPriorityList implements IPartitionList {

    private final Set<TagKey<?>> tagKeys;
    private final String tagExp;
    private final LoadingCache<AEKey, Boolean> accept = CacheBuilder.newBuilder().build(
            new CacheLoader<>() {
                @Override
                public @NotNull Boolean load(@NotNull AEKey key) {
                    for (var tag : tagKeys) {
                        if (key.isTagged(tag)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    public TagPriorityList(Set<TagKey<?>> tagKeys, String tagExp) {
        this.tagKeys = tagKeys;
        this.tagExp = tagExp;
    }

    @Override
    public boolean isListed(AEKey input) {
        return this.accept.getUnchecked(input);
    }

    @Override
    public boolean isEmpty() {
        return this.tagExp.isEmpty();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }

}
