package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.client.render.AERenderTypes;
import appeng.client.renderer.blockentity.CellLedRenderer;
import appeng.client.renderer.blockentity.ChestOrDriveRenderState;
import com.glodblock.github.extendedae.client.model.ExDriveModel;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ExDriveTESR extends ExBaseTESR<TileExDrive, ChestOrDriveRenderState> {

    public ExDriveTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ChestOrDriveRenderState createRenderState() {
        return new ChestOrDriveRenderState();
    }

    @Override
    public void extractRenderState(TileExDrive drive, ChestOrDriveRenderState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(drive, state, partialTicks, cameraPos, crumblingOverlay);
        if (drive.getCellCount() != 20) {
            throw new IllegalStateException("Expected extended drive to have 20 slots");
        }
        var blockOrientation = BlockOrientation.get(drive);
        state.extract(blockOrientation, drive, partialTicks);
    }

    @Override
    public void submit(ChestOrDriveRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        this.applyBlockSpinTransform(poseStack, state.blockOrientation);
        var slotTranslation = new Vector3f();
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                poseStack.pushPose();
                ExDriveModel.getSlotOrigin(row, col, slotTranslation);
                poseStack.translate(slotTranslation.x(), slotTranslation.y(), slotTranslation.z());
                int slot = row * 2 + col;
                nodes.submitCustomGeometry(
                        poseStack,
                        AERenderTypes.STORAGE_CELL_LEDS,
                        (pose, consumer) -> CellLedRenderer.renderLed(state.cellColors[slot], consumer, pose)
                );
                poseStack.popPose();
            }
        }
        poseStack.popPose();

        poseStack.pushPose();
        var back = state.blockOrientation.getSide(RelativeSide.BACK);
        var oppoOrientation = BlockOrientation.get(back, state.blockOrientation.getSpin());
        this.applyBlockSpinTransform(poseStack, oppoOrientation);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                poseStack.pushPose();
                ExDriveModel.getSlotOrigin(row, col, slotTranslation);
                poseStack.translate(slotTranslation.x(), slotTranslation.y(), slotTranslation.z());
                int slot = row * 2 + col + 10;
                nodes.submitCustomGeometry(
                        poseStack,
                        AERenderTypes.STORAGE_CELL_LEDS,
                        (pose, consumer) -> CellLedRenderer.renderLed(state.cellColors[slot], consumer, pose)
                );
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

}
