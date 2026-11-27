package com.glodblock.github.extendedae.client.render.tesr.helper;

import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public record DummyTileOwner(BlockEntity te) implements ItemOwner {

    @Override
    public @NotNull Level level() {
        return this.te.getLevel();
    }

    @Override
    public @NotNull Vec3 position() {
        return Vec3.atCenterOf(this.te.getBlockPos());
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 0;
    }

}
