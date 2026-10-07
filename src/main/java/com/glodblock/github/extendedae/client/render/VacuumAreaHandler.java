package com.glodblock.github.extendedae.client.render;

import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class VacuumAreaHandler {

    private static final List<AABBData> PENDING = Collections.synchronizedList(new ArrayList<>());
    private static final ColorData AREA_COLOR_DARK = new ColorData(1F, 0F, 0.8F, 0F);
    private static final ColorData AREA_COLOR = new ColorData(0.4F, 0F, 0.8F, 0F);
    private static final RenderType FACE_RENDER = RenderType.create(
            "extendedae_face",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false, false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderType.NO_CULL)
                    .setShaderState(RenderType.POSITION_COLOR_SHADER)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(RenderType.COLOR_WRITE)
                    .setLightmapState(RenderType.NO_LIGHTMAP)
                    .setTextureState(RenderType.NO_TEXTURE)
                    .createCompositeState(true)
    );
    private static final RenderType LINE_RENDER = RenderType.create(
            "extendedae_line",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256,
            false, false,
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setCullState(RenderType.NO_CULL)
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
    );

    public static void addJob(Level world, AABB area) {
        PENDING.add(new AABBData(world, area));
    }

    public static void hook(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            run(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getCamera());
        }
    }

    private static void run(PoseStack stack, MultiBufferSource.BufferSource multiBuf, Camera camera) {
        if (PENDING.isEmpty()) {
            return;
        }
        var current = Minecraft.getInstance().level;
        if (current == null || !camera.isInitialized()) {
            PENDING.clear();
            return;
        }
        stack.pushPose();
        var offset = camera.getPosition().reverse();
        stack.translate(offset.x, offset.y, offset.z);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        for (var info : PENDING) {
            if (info.world == current) {
                renderArea(info.area, stack, multiBuf);
            }
        }
        multiBuf.endBatch();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        stack.popPose();
        PENDING.clear();
    }

    private static void renderArea(AABB area, PoseStack ms, MultiBufferSource.BufferSource bufferIn) {
        RenderSystem.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
        LevelRenderer.renderLineBox(ms, bufferIn.getBuffer(LINE_RENDER), area, AREA_COLOR_DARK.getRf(), AREA_COLOR_DARK.getGf(), AREA_COLOR_DARK.getBf(), AREA_COLOR_DARK.getAf());
        drawCube(area, ms, bufferIn);
    }

    private static void drawCube(AABB box, PoseStack stack, MultiBufferSource multiBuf) {
        float x1 = (float) box.minX;
        float x2 = (float) box.maxX;
        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;
        float z1 = (float) box.minZ;
        float z2 = (float) box.maxZ;
        var red = AREA_COLOR.getRf();
        var green = AREA_COLOR.getGf();
        var blue = AREA_COLOR.getBf();
        var alpha = AREA_COLOR.getAf();

        var matrix = stack.last().pose();
        var buffer = multiBuf.getBuffer(FACE_RENDER);

        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
    }

    private record AABBData(Level world, AABB area) { }

}
