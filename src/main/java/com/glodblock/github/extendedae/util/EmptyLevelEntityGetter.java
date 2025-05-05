package com.glodblock.github.extendedae.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public final class EmptyLevelEntityGetter<T extends EntityAccess> implements LevelEntityGetter<T> {
    public EmptyLevelEntityGetter() {
    }

    @Nullable
    public T get(int p_156931_) {
        return null;
    }

    @Nullable
    public T get(@Nonnull UUID p_156939_) {
        return null;
    }

    @Nonnull
    public Iterable<T> getAll() {
        return ImmutableList.of();
    }

    public <U extends T> void get(@NotNull EntityTypeTest<T, U> p_156935_, @NotNull AbortableIterationConsumer<U> p_261602_) {
    }

    public void get(@Nonnull AABB p_156937_, @Nonnull Consumer<T> p_156938_) {
    }

    public <U extends T> void get(@NotNull EntityTypeTest<T, U> p_156932_, @NotNull AABB p_156933_, @NotNull AbortableIterationConsumer<U> p_261542_) {
    }
}
