package com.github.glodblock.epp.mixins.mek;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.render.transmitter.RenderMechanicalPipe;
import mekanism.common.tile.transmitter.TileEntityMechanicalPipe;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderMechanicalPipe.class)
public abstract class MixinRenderMechanicalPipe {

    @Inject(
            method = "render(Lmekanism/common/tile/transmitter/TileEntityMechanicalPipe;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void safeRender(TileEntityMechanicalPipe tile, float partialTick, PoseStack matrix, MultiBufferSource renderer, int light, int overlayLight, ProfilerFiller profiler, CallbackInfo ci) {
        if (tile.getTransmitter().getTransmitterNetwork() == null) {
            ci.cancel();
        }
    }

}
