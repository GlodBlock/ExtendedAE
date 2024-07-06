package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.orientation.BlockOrientation;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileExInscriber;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class ExInscriberTESR implements BlockEntityRenderer<TileExInscriber> {

    private static final float ITEM_RENDER_SCALE = 0.5f;

    private static final float[][] offset = new float[][] {
            {0.1875f, 0.1875f}, {0.1875f, -0.1875f}, {-0.1875f, 0.1875f}, {-0.1875f, -0.1875f}
    };

    private static final Material TEXTURE_INSIDE = new Material(InventoryMenu.BLOCK_ATLAS, ExtendedAE.id("block/extended_inscriber/ex_inscriber_inside"));

    public ExInscriberTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull TileExInscriber blockEntity, float partialTicks, PoseStack ms, @NotNull MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
        ms.pushPose();
        ms.translate(0.5F, 0.5F, 0.5F);
        BlockOrientation orientation = BlockOrientation.get(blockEntity);
        ms.mulPose(orientation.getQuaternion());
        ms.translate(-0.5F, -0.5F, -0.5F);

        // render sides of stamps

        long absoluteProgress = 0;

        if (blockEntity.isSmash()) {
            final long currentTime = System.currentTimeMillis();
            absoluteProgress = currentTime - blockEntity.getClientStart();
            if (absoluteProgress > 800) {
                blockEntity.setSmash(false);
            }
        }

        final float relativeProgress = absoluteProgress % 800 / 400.0f;
        float progress = relativeProgress;

        if (progress > 1.0f) {
            progress = 1.0f - easeDecompressMotion(progress - 1.0f);
        } else {
            progress = easeCompressMotion(progress);
        }

        float press = 0.2f;
        press -= progress / 5.0f;

        float middle = 0.5f;
        middle += 0.02f;
        final float TwoPx = 2.0f / 16.0f;
        final float base = 0.4f;

        final TextureAtlasSprite tas = TEXTURE_INSIDE.sprite();

        VertexConsumer buffer = buffers.getBuffer(RenderType.solid());

        // Bottom of Top Stamp
        addVertex(buffer, ms, tas, TwoPx, middle + press, TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.DOWN);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight,
                Direction.DOWN);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, 1.0f - TwoPx, 0.125f, 0.875f, combinedOverlay, combinedLight,
                Direction.DOWN);
        addVertex(buffer, ms, tas, TwoPx, middle + press, 1.0f - TwoPx, 0.875f, 0.875f, combinedOverlay, combinedLight,
                Direction.DOWN);

        // Front of Top Stamp
        addVertex(buffer, ms, tas, TwoPx, middle + base, TwoPx, 0.125f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + base, TwoPx, 0.875f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle + press, TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight, Direction.NORTH);

        // Rear of Top Stamp
        addVertex(buffer, ms, tas, TwoPx, middle + base, 1.0f - TwoPx, 0.125f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.SOUTH);
        addVertex(buffer, ms, tas, TwoPx, middle + press, 1.0f - TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight,
                Direction.SOUTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + press, 1.0f - TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.SOUTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle + base, 1.0f - TwoPx, 0.875f, 0.125f - (press - base),
                combinedOverlay,
                combinedLight, Direction.SOUTH);

        // Top of Bottom Stamp
        middle -= 2.0f * 0.02f;
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.UP);
        addVertex(buffer, ms, tas, TwoPx, middle - press, TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight, Direction.UP);
        addVertex(buffer, ms, tas, TwoPx, middle - press, 1.0f - TwoPx, 0.125f, 0.875f, combinedOverlay, combinedLight,
                Direction.UP);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, 1.0f - TwoPx, 0.875f, 0.875f, combinedOverlay, combinedLight,
                Direction.UP);

        // Front of Bottom Stamp
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - base, TwoPx, 0.125f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle - base, TwoPx, 0.875f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.NORTH);
        addVertex(buffer, ms, tas, TwoPx, middle - press, TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.NORTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight,
                Direction.NORTH);

        // Rear of Bottom Stamp
        addVertex(buffer, ms, tas, TwoPx, middle - press, 1.0f - TwoPx, 0.875f, 0.125f, combinedOverlay, combinedLight,
                Direction.SOUTH);
        addVertex(buffer, ms, tas, TwoPx, middle - base, 1.0f - TwoPx, 0.875f, 0.125f - (press - base), combinedOverlay,
                combinedLight, Direction.SOUTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - base, 1.0f - TwoPx, 0.125f, 0.125f - (press - base),
                combinedOverlay,
                combinedLight, Direction.SOUTH);
        addVertex(buffer, ms, tas, 1.0f - TwoPx, middle - press, 1.0f - TwoPx, 0.125f, 0.125f, combinedOverlay, combinedLight,
                Direction.SOUTH);

        // render items.

        if (!EAEConfig.disableInscriberRender) {
            for (int x = 0; x < TileExInscriber.MAX_THREAD; x ++) {
                var inv = blockEntity.getIndexInventory(x);

                int items = 0;
                if (!inv.getStackInSlot(0).isEmpty()) {
                    items++;
                }
                if (!inv.getStackInSlot(1).isEmpty()) {
                    items++;
                }
                if (!inv.getStackInSlot(2).isEmpty()) {
                    items++;
                }

                boolean renderPresses;
                if (relativeProgress > 1.0f || items == 0) {
                    // When crafting completes, don't render the presses (they may have been
                    // consumed, see below)
                    renderPresses = false;

                    ItemStack is = inv.getStackInSlot(3);

                    if (is.isEmpty()) {
                        final InscriberRecipe ir = blockEntity.getTask(x);
                        if (ir != null) {
                            // The "PRESS" type will consume the presses, so they should not render after
                            // completing
                            // the press animation
                            renderPresses = ir.getProcessType() == InscriberProcessType.INSCRIBE;
                            is = ir.getResultItem().copy();
                        }
                    }
                    this.renderItem(ms, is, x, 0.0f, buffers, combinedLight, combinedOverlay, blockEntity.getLevel());
                } else {
                    renderPresses = true;
                    this.renderItem(ms, inv.getStackInSlot(2), x, 0.0f, buffers, combinedLight, combinedOverlay,
                            blockEntity.getLevel());
                }

                if (renderPresses) {
                    this.renderItem(ms, inv.getStackInSlot(0), x, press, buffers, combinedLight, combinedOverlay,
                            blockEntity.getLevel());
                    this.renderItem(ms, inv.getStackInSlot(1), x, -press, buffers, combinedLight, combinedOverlay,
                            blockEntity.getLevel());
                }
            }
        }

        ms.popPose();
    }

    private static void addVertex(VertexConsumer vb, PoseStack ms, TextureAtlasSprite sprite, float x, float y, float z, double texU, double texV, int overlayUV, int lightmapUV, Direction front) {
        vb.addVertex(ms.last().pose(), x, y, z);
        vb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        vb.setUv(sprite.getU((float) texU), sprite.getV((float) texV));
        vb.setOverlay(overlayUV);
        vb.setLight(lightmapUV);
        vb.setNormal(ms.last(), front.getStepX(), front.getStepY(), front.getStepZ());
    }

    private void renderItem(PoseStack ms, ItemStack stack, int index, float o, MultiBufferSource buffers, int combinedLight, int combinedOverlay, Level level) {
        if (!stack.isEmpty()) {
            ms.pushPose();
            // move to center
            ms.translate(0.5f + offset[index][0], 0.5f + o, 0.5f + offset[index][1]);
            ms.mulPose(new Quaternionf().rotationX(Mth.DEG_TO_RAD * 90));
            // set scale
            ms.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, ITEM_RENDER_SCALE);

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            // heuristic to scale items down much further than blocks,
            // the assumption here is that the generated item models will return their faces
            // for direction=null, while a block-model will have their faces for
            // cull-faces, but not direction=null
            var model = itemRenderer.getItemModelShaper().getItemModel(stack);
            var quads = model.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null);
            // Note: quads may be null for mods implementing FabricBakedModel without caring about getQuads.
            if (!quads.isEmpty()) {
                ms.scale(0.5f, 0.5f, 0.5f);
            }

            RenderSystem.applyModelViewMatrix();
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, ms, buffers, level, 0);
            ms.popPose();
        }
    }

    private static float easeCompressMotion(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }

    private static float easeDecompressMotion(float x) {
        return x * x * x * x * x;
    }

}
