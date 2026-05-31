package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import com.glodblock.github.extendedae.client.render.tesr.helper.DummyTileOwner;
import com.glodblock.github.extendedae.client.render.tesr.state.SingleItemState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public abstract class ExBaseTESR<T extends BlockEntity, S extends BlockEntityRenderState> implements BlockEntityRenderer<@NotNull T, @NotNull S> {

    protected final ItemModelResolver itemResolver;

    public ExBaseTESR(BlockEntityRendererProvider.Context context) {
        this.itemResolver = context.itemModelResolver();
    }

    public void setupItemModel(ItemStackRenderState output, ItemStack stack, T host) {
        if (!stack.isEmpty()) {
            this.itemResolver.updateForTopItem(
                    output,
                    stack,
                    ItemDisplayContext.GROUND,
                    host.getLevel(),
                    new DummyTileOwner(host),
                    host.getBlockPos().hashCode()
            );
        }
    }

    public void applyBlockSpinTransform(PoseStack poseStack, BlockOrientation orientation) {
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(orientation.getQuaternion());
        poseStack.translate(-0.5, -0.5, -0.5);
    }

    public void renderSingleItem(SingleItemState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector nodes) {
        if (state.item.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.applyBlockSpinTransform(poseStack, state.blockOrientation);
        poseStack.mulPose(state.transform.getMatrix());
        state.item.submit(poseStack, nodes, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

}
