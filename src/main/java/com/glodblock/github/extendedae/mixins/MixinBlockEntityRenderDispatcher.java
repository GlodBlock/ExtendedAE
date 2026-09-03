package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.util.helper.TESRProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher implements TESRProvider {

    @Shadow
    public abstract <E extends BlockEntity> void render(E tile, float partialTick, PoseStack ms, MultiBufferSource buffer);

    @Override
    public <E extends BlockEntity> Runnable getRenderJob(E tile, float partialTick, PoseStack ms, MultiBufferSource buffer) {
        return () -> this.render(tile, partialTick, ms, buffer);
    }

}
