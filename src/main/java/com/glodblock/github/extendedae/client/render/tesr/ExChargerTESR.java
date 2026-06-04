package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import com.glodblock.github.extendedae.client.render.tesr.state.MultiItemState;
import com.glodblock.github.extendedae.common.tileentities.TileExCharger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExChargerTESR extends ExBaseTESR<TileExCharger, MultiItemState> {

    private static final float HALF_PI = (float) (Math.PI / 2f);
    private static final float[] X = new float[] {0.27f, 0.4f, 0.6f, 0.73f};

    public ExChargerTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TileExCharger be, MultiItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.blockOrientation = BlockOrientation.get(be);
        state.clearItems();
        setupItem(state, be, 0);
        setupItem(state, be, 1);
        setupItem(state, be, 2);
        setupItem(state, be, 3);
    }

    private void setupItem(MultiItemState state, TileExCharger te, int index) {
        var bo = new Quaternionf().rotateYXZ(HALF_PI, 0, 0);
        Transformation transform = new Transformation(new Vector3f(X[index], 0.375f, 0.5f), bo, null, null);
        state.setupItem(item -> this.setupItemModel(item, te.getInternalInventory().getStackInSlot(index), te), transform);
    }

    @Override
    public @NotNull MultiItemState createRenderState() {
        return new MultiItemState();
    }

    @Override
    public void submit(@NotNull MultiItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState camera) {
        if (state.items.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.applyBlockSpinTransform(poseStack, state.blockOrientation);
        for (var item : state.items) {
            poseStack.pushPose();
            poseStack.mulPose(item.transform().getMatrix());
            item.item().submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

}
