package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.stacks.AEItemKey;
import com.glodblock.github.extendedae.client.render.tesr.state.SingleItemState;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
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

public class IngredientBufferTESR extends ExBaseTESR<TileIngredientBuffer, SingleItemState> {

    private static final Transformation T = new Transformation(new Vector3f(0.5f, 0.25f, 0.5f), null, null, null);

    public IngredientBufferTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull SingleItemState createRenderState() {
        return new SingleItemState();
    }

    @Override
    public void extractRenderState(TileIngredientBuffer be, SingleItemState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        var inv = be.getGenericInv();
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (stack != null && stack.what() instanceof AEItemKey itemKey && !itemKey.toStack().isEmpty()) {
                state.item.clear();
                state.transform = T;
                var itemStack = itemKey.toStack();
                this.setupItemModel(state.item, itemStack, be);
                break;
            }
        }
    }

    @Override
    public void submit(@NotNull SingleItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes, @NotNull CameraRenderState camera) {
        this.renderSingleItem(state, poseStack, nodes);
    }

}
