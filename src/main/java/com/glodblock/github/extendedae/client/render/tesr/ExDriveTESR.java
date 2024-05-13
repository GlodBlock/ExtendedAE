package com.glodblock.github.extendedae.client.render.tesr;

import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.orientation.BlockOrientation;
import appeng.api.storage.cells.CellState;
import com.glodblock.github.extendedae.client.model.ExDriveBakedModel;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.EnumMap;

public class ExDriveTESR implements BlockEntityRenderer<TileExDrive> {

    public ExDriveTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileExDrive drive, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource buffers, int combinedLightIn, int combinedOverlayIn) {

        if (drive.getCellCount() != 20) {
            throw new IllegalStateException("Expected extended drive to have 20 slots");
        }

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        var blockOrientation = BlockOrientation.get(drive);
        ms.mulPose(blockOrientation.getQuaternion());
        ms.translate(-0.5, -0.5, -0.5);

        var buffer = buffers.getBuffer(CellLedRenderer.RENDER_LAYER);

        Vector3f slotTranslation = new Vector3f();
        for (int disk = 0; disk < 2; disk ++) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 2; col++) {
                    ms.pushPose();

                    ExDriveBakedModel.getSlotOrigin(row, col, disk, slotTranslation);
                    ms.translate(slotTranslation.x(), slotTranslation.y(), slotTranslation.z());
                    int slot = row * 2 + col + disk * 10;
                    CellLedRenderer.renderLed(drive, slot, buffer, ms);

                    ms.popPose();
                }
            }
        }

        ms.popPose();
    }

    private static class CellLedRenderer {

        private static final EnumMap<CellState, Vector3f> STATE_COLORS;

        // Color to use if the cell is present but unpowered
        private static final Vector3f UNPOWERED_COLOR = new Vector3f(0, 0, 0);

        // Color used for the cell indicator for blinking during recent activity
        private static final Vector3f BLINK_COLOR = new Vector3f(1, 0.5f, 0.5f);

        static {
            STATE_COLORS = new EnumMap<>(CellState.class);
            for (var cellState : CellState.values()) {
                var color = cellState.getStateColor();
                var colorVector = new Vector3f(
                        ((color >> 16) & 0xFF) / 255.0f,
                        ((color >> 8) & 0xFF) / 255.0f,
                        (color & 0xFF) / 255.0f);
                STATE_COLORS.put(cellState, colorVector);
            }
        }

        // The positions are based on the upper left slot in a drive
        private static final float L = 5 / 16.f; // left (x-axis)
        private static final float R = 4 / 16.f; // right (x-axis)
        private static final float T = 1 / 16.f; // top (y-axis)
        private static final float B = -0.001f / 16.f; // bottom (y-axis)
        private static final float FR = -0.001f / 16.f; // front (z-axis)
        private static final float BA = 0.499f / 16.f; // back (z-axis)

        // Vertex data for the LED cuboid (has no back)
        // Directions are when looking from the front onto the LED
        private static final float[] LED_QUADS = {
                // Front Face
                R, T, FR, L, T, FR, L, B, FR, R, B, FR,
                // Left Face
                L, T, FR, L, T, BA, L, B, BA, L, B, FR,
                // Right Face
                R, T, BA, R, T, FR, R, B, FR, R, B, BA,
                // Top Face
                R, T, BA, L, T, BA, L, T, FR, R, T, FR,
                // Bottom Face
                R, B, FR, L, B, FR, L, B, BA, R, B, BA, };

        public static final RenderType RENDER_LAYER = RenderType.create("extended_drive_leds",
                DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 32565, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                        .createCompositeState(false));

        public static void renderLed(IChestOrDrive drive, int slot, VertexConsumer buffer, PoseStack ms) {

            Vector3f color = getColorForSlot(drive, slot);
            if (color == null) {
                return;
            }

            for (int i = 0; i < LED_QUADS.length; i += 3) {
                float x = LED_QUADS[i];
                float y = LED_QUADS[i + 1];
                float z = LED_QUADS[i + 2];
                buffer.vertex(ms.last().pose(), x, y, z).color(color.x(), color.y(), color.z(), 1.f)
                        .endVertex();
            }
        }

        private static Vector3f getColorForSlot(IChestOrDrive drive, int slot) {
            var state = drive.getCellStatus(slot);
            if (state == CellState.ABSENT) {
                return null;
            }
            if (!drive.isPowered()) {
                return UNPOWERED_COLOR;
            }
            Vector3f col = STATE_COLORS.get(state);
            if (drive.isCellBlinking(slot)) {
                // 200 ms interval (100ms to get to red, then 100ms back)
                long t = System.currentTimeMillis() % 200;
                float f = (t - 100) / 200.0f + 0.5f;
                f = easeInOutCubic(f);
                col = new Vector3f(col);
                col.lerp(BLINK_COLOR, f);
            }

            return col;
        }

        private static float easeInOutCubic(float x) {
            return x < 0.5f ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2;
        }

        private CellLedRenderer() {
        }

    }


}
