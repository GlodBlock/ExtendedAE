package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import com.glodblock.github.extendedae.client.render.tesr.state.SingleItemState;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalFixer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrystalFixerTESR extends ExBaseTESR<TileCrystalFixer, SingleItemState> {

    private static final float HALF_PI = (float) (Math.PI / 2f);
    private static final Transformation T = new Transformation(
            new Vector3f(0.5f, 0.5f, 0.4f),
            new Quaternionf().rotateYXZ(0, -HALF_PI, 0),
            null, null
    );

    public CrystalFixerTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TileCrystalFixer be, SingleItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.blockOrientation = BlockOrientation.get(be);
        state.transform = T;
        state.item.clear();
        this.setupItemModel(state.item, be.getInternalInventory().getStackInSlot(0), be);
    }

    @Override
    public @NotNull SingleItemState createRenderState() {
        return new SingleItemState();
    }

    @Override
    public void submit(@NotNull SingleItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState camera) {
        this.renderSingleItem(state, poseStack, nodes);
    }

}
