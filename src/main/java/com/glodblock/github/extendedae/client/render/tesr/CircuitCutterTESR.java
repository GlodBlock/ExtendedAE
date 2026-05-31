package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import com.glodblock.github.extendedae.client.render.tesr.state.SingleItemState;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CircuitCutterTESR extends ExBaseTESR<TileCircuitCutter, SingleItemState> {

    private static final float HALF_PI = (float) (Math.PI / 2f);

    public CircuitCutterTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TileCircuitCutter be, SingleItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.blockOrientation = BlockOrientation.get(be);
        float progress = (float) GlodUtil.clamp((double) be.getProgress() / TileCircuitCutter.MAX_PROGRESS, 0, 1);
        var stack = progress > 0.5 ? be.getRenderOutput() : be.getInput().getStackInSlot(0);
        state.transform = getTransformer(progress * 0.5f + 0.25f, stack.getItem() instanceof BlockItem);
        state.item.clear();
        this.setupItemModel(state.item, stack, be);
    }

    @Override
    public SingleItemState createRenderState() {
        return new SingleItemState();
    }

    @Override
    public void submit(SingleItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState camera) {
        this.renderSingleItem(state, poseStack, nodes);
    }

    private static Transformation getTransformer(float offset, boolean isBlock) {
        return isBlock ?
                new Transformation(
                        new Vector3f(offset, 0.5f, 0.66f),
                        new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
                        null, null) :
                new Transformation(
                        new Vector3f(offset, 0.5f, 0.63f),
                        new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
                        null, null);
    }

}
