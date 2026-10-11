package com.glodblock.github.extendedae.util.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public interface TESRProvider {

    default <S extends BlockEntityRenderState> Runnable getRenderJob(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        return () -> {};
    }

}
