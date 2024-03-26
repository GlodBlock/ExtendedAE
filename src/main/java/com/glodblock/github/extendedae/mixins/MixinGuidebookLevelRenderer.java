package com.glodblock.github.extendedae.mixins;

import appeng.client.guidebook.scene.GuidebookLevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuidebookLevelRenderer.class)
public abstract class MixinGuidebookLevelRenderer {

    @Inject(
            method = "handleBlockEntity",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private <E extends BlockEntity> void check(PoseStack stack, E blockEntity, MultiBufferSource buffers, CallbackInfo ci) {
        var render = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (render == null || !render.shouldRender(blockEntity, blockEntity.getBlockPos().getCenter())) {
            ci.cancel();
        }
    }

}
