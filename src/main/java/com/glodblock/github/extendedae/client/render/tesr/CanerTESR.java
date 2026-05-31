package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import com.glodblock.github.extendedae.client.render.tesr.state.SingleItemState;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CanerTESR extends ExBaseTESR<@NotNull TileCaner, @NotNull SingleItemState> {

    private static final Transformation T = new Transformation(new Vector3f(0.5f, 0.375f, 0.5f), null, null, null);

    public CanerTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TileCaner be, SingleItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.blockOrientation = BlockOrientation.get(be);
        state.transform = T;
        state.item.clear();
        this.setupItemModel(state.item, be.getContainer().getStackInSlot(0), be);
    }

    @Override
    public SingleItemState createRenderState() {
        return new SingleItemState();
    }

    @Override
    public void submit(SingleItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState camera) {
        this.renderSingleItem(state, poseStack, nodes);
    }

}
