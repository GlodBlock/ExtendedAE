package com.glodblock.github.extendedae.mixins.mek;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.transmitter.RenderUniversalCable;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderUniversalCable.class)
public abstract class MixinRenderUniversalCable {

    @Inject(
            method = "render(Lmekanism/common/tile/transmitter/TileEntityUniversalCable;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void safeRender(TileEntityUniversalCable tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler, CallbackInfo ci) {
        if (tile.getTransmitter().getTransmitterNetwork() == null) {
            ci.cancel();
        }
    }

}
