package com.github.glodblock.extendedae.client.render.tesr;

import appeng.api.stacks.AEItemKey;
import com.github.glodblock.extendedae.common.tileentities.TileIngredientBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class IngredientBufferTESR implements BlockEntityRenderer<TileIngredientBuffer> {

    public IngredientBufferTESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@NotNull TileIngredientBuffer tile, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource buffers,
                       int combinedLight, int combinedOverlay) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var inv = tile.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (stack != null && stack.what() instanceof AEItemKey itemKey && !itemKey.toStack().isEmpty()) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5D, 0.25D, 0.5D);
                var itemStack = itemKey.toStack();
                itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, matrixStackIn, buffers, tile.getLevel(), 0);
                matrixStackIn.popPose();
                break;
            }
        }
        RenderSystem.disableBlend();
    }

}
