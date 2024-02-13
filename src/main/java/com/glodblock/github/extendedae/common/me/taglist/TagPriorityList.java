package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class TagPriorityList implements IPartitionList {

    private final Set<TagKey<?>> whiteSet;
    private final Set<TagKey<?>> blackSet;
    private final String tagExp;
    private final LoadingCache<Object, Boolean> accept = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(4000)
            .build(
            new CacheLoader<>() {
                @Override
                public @NotNull Boolean load(@NotNull Object obj) {
                    Holder<?> refer = null;
                    if (obj instanceof Item item) {
                        refer = ForgeRegistries.ITEMS.getHolder(item).orElse(null);
                    } else if (obj instanceof Fluid fluid) {
                        refer = ForgeRegistries.FLUIDS.getHolder(fluid).orElse(null);
                    }
                    if (refer != null) {
                        if (whiteSet.isEmpty()) {
                            return false;
                        }
                        boolean pass = refer.tags().anyMatch(whiteSet::contains);
                        if (pass) {
                            if (!blackSet.isEmpty()) {
                                return refer.tags().noneMatch(blackSet::contains);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            }
    );

    public TagPriorityList(Set<TagKey<?>> whiteKeys, Set<TagKey<?>> blackKeys, String tagExp) {
        this.whiteSet = whiteKeys;
        this.blackSet = blackKeys;
        this.tagExp = tagExp;
    }

    @Override
    public boolean isListed(AEKey input) {
        return this.accept.getUnchecked(input.getPrimaryKey());
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
