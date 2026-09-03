package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.client.render.StageTESRTile;
import com.glodblock.github.extendedae.util.SequenceJobs;
import com.glodblock.github.extendedae.util.helper.RenderJobProvider;
import com.glodblock.github.extendedae.util.helper.TESRProvider;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer implements RenderJobProvider {

    @Unique
    private final Map<RenderLevelStageEvent.Stage, SequenceJobs> stageRenderJobs = new IdentityHashMap<>();

    @Inject(
            method = "renderLevel",
            at = @At("HEAD")
    )
    private void setup(DeltaTracker p_348530_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_, Matrix4f p_323920_, CallbackInfo ci) {
        this.stageRenderJobs.clear();
    }

    @WrapOperation(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V")
    )
    private <E extends BlockEntity> void collect(BlockEntityRenderDispatcher instance, E tile, float p_112269_, PoseStack p_112270_, MultiBufferSource p_112271_, Operation<Void> original) {
        if (tile instanceof StageTESRTile stg && instance instanceof TESRProvider provider) {
            var job = this.stageRenderJobs.computeIfAbsent(stg.renderStage(), k -> new SequenceJobs());
            job.add(provider.getRenderJob(tile, p_112269_, p_112270_, p_112271_));
        } else {
            original.call(instance, tile, p_112269_, p_112270_, p_112271_);
        }
    }

    @Override
    public Runnable getRenderJob(RenderLevelStageEvent.Stage stage) {
        return this.stageRenderJobs.getOrDefault(stage, new SequenceJobs());
    }

}
