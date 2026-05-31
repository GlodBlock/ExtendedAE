package com.glodblock.github.extendedae.client.render.tesr;

import appeng.core.AEConfig;
import appeng.core.particles.ParticleTypes;
import com.glodblock.github.extendedae.client.render.tesr.state.BlockItemState;
import com.glodblock.github.extendedae.common.tileentities.TileExMolecularAssembler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExMolecularAssemblerTESR extends ExBaseTESR<TileExMolecularAssembler, BlockItemState> {

    public ExMolecularAssemblerTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull BlockItemState createRenderState() {
        return new BlockItemState();
    }

    @Override
    public void extractRenderState(TileExMolecularAssembler be, BlockItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.item.clear();
        var status = be.getAnimationStatus();
        if (status != null) {
            if (!Minecraft.getInstance().isPaused()) {
                if (status.isExpired()) {
                    be.setAnimationStatus(null);
                }
                status.setAccumulatedTicks(status.getAccumulatedTicks() + partialTicks);
                status.setTicksUntilParticles(status.getTicksUntilParticles() - partialTicks);
            }
            double centerX = be.getBlockPos().getX() + 0.5f;
            double centerY = be.getBlockPos().getY() + 0.5f;
            double centerZ = be.getBlockPos().getZ() + 0.5f;
            var is = status.getIs();
            // Spawn crafting FX that fly towards the block's center
            var level = be.getLevel();
            if (AEConfig.instance().isEnableEffects() && level != null) {
                if (status.getTicksUntilParticles() <= 0) {
                    status.setTicksUntilParticles(4);
                    for (int x = 0; x < (int) Math.ceil(status.getSpeed() / 5.0); x++) {
                        level.addParticle(
                                new ItemParticleOption(ParticleTypes.CRAFTING, ItemStackTemplate.fromNonEmptyStack(is)),
                                centerX,
                                centerY, centerZ,
                                0,
                                0, 0);
                    }
                }
            }
            this.setupItemModel(state.item, is, be);
            state.isBlock = (is.getItem() instanceof BlockItem);
        }
    }

    @Override
    public void submit(BlockItemState state, PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5); // Translate to center of block
        if (!state.isBlock) {
            poseStack.translate(0, -0.3f, 0);
        } else {
            poseStack.translate(0, -0.2f, 0);
        }
        state.item.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

}
