package com.glodblock.github.extendedae.util.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TESRProvider {

    default <E extends BlockEntity> Runnable getRenderJob(E tile, float partialTick, PoseStack ms, MultiBufferSource buffer) {
        return () -> {};
    }

}
