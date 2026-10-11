package com.glodblock.github.extendedae.client.render.tesr;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.render.tesr.state.VacuumInterfaceState;
import com.glodblock.github.extendedae.common.tileentities.TileVacuumInterface;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VacuumInterfaceTESR extends ExBaseTESR<@NotNull TileVacuumInterface, @NotNull VacuumInterfaceState> {

    private static final ColorData AREA_COLOR_DARK = new ColorData(1F, 0F, 0.8F, 0F);
    private static final ColorData AREA_COLOR = new ColorData(0.4F, 0F, 0.8F, 0F);
    private static final RenderPipeline.Snippet SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withDepthStencilState(new DepthStencilState(CompareOp.NOT_EQUAL, true))
            .withCull(false)
            .buildSnippet();
    private static final RenderType FACE_RENDER = RenderType.create(
            ExtendedAE.stringId("vacuum_face"),
            RenderSetup.builder(RenderPipeline.builder(SNIPPET)
                            .withVertexShader("core/position_color")
                            .withFragmentShader("core/position_color")
                            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                            .withLocation(ExtendedAE.id("vacuum_face_pipeline"))
                            .build())
                    .bufferSize(256)
                    .createRenderSetup()
    );
    private final RenderType LINE_RENDER = RenderType.create(
            ExtendedAE.stringId("vacuum_line"),
            RenderSetup.builder(RenderPipeline.builder(SNIPPET)
                            .withVertexShader("core/rendertype_lines")
                            .withFragmentShader("core/rendertype_lines")
                            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
                            .withColorTargetState(new ColorTargetState(BlendFunction.GLINT))
                            .withLocation(ExtendedAE.id("vacuum_line_pipeline"))
                            .build())
                    .bufferSize(256)
                    .createRenderSetup()
    );

    public VacuumInterfaceTESR(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(TileVacuumInterface tile, VacuumInterfaceState state, float partialTicks, @NotNull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(tile, state, partialTicks, cameraPos, crumblingOverlay);
        if (tile.isDisplayArea()) {
            state.shouldRender = true;
            state.area = tile.getWorkArea();
        } else {
            state.shouldRender = false;
        }
    }

    @Override
    public void submit(VacuumInterfaceState state, @NotNull PoseStack ms, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState camera) {
        if (state.shouldRender) {
            if (state.area != null && camera.initialized) {
                var area = state.area.move(state.blockPos.multiply(-1)).inflate(0.001);
                ms.pushPose();
                submitNodeCollector.submitCustomGeometry(ms, LINE_RENDER, (pose, consumer) -> this.drawBlockOutline(area, pose, consumer));
                submitNodeCollector.submitCustomGeometry(ms, FACE_RENDER, (pose, consumer) -> this.drawCube(area, pose, consumer));
                ms.popPose();
            }
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull TileVacuumInterface tile) {
        return tile.getWorkArea();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    private void drawCube(AABB box, PoseStack.Pose matrix, VertexConsumer buffer) {
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

    private void drawBlockOutline(AABB aabb, PoseStack.Pose stack, VertexConsumer buf) {
        var r = VacuumInterfaceTESR.AREA_COLOR_DARK.getRf();
        var g = VacuumInterfaceTESR.AREA_COLOR_DARK.getGf();
        var b = VacuumInterfaceTESR.AREA_COLOR_DARK.getBf();
        var a = VacuumInterfaceTESR.AREA_COLOR_DARK.getAf();
        var topRight = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
        var bottomRight = new Vec3(aabb.maxX, aabb.minY, aabb.maxZ);
        var bottomLeft = new Vec3(aabb.minX, aabb.minY, aabb.maxZ);
        var topLeft = new Vec3(aabb.minX, aabb.maxY, aabb.maxZ);
        var topRight2 = new Vec3(aabb.maxX, aabb.maxY, aabb.minZ);
        var bottomRight2 = new Vec3(aabb.maxX, aabb.minY, aabb.minZ);
        var bottomLeft2 = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
        var topLeft2 = new Vec3(aabb.minX, aabb.maxY, aabb.minZ);
        renderBox(buf, stack, topLeft, bottomLeft, topRight, bottomRight, r, g, b, a);
        renderBox(buf, stack, topLeft2, bottomLeft2, topRight2, bottomRight2, r, g, b, a);
        renderLine(buf, stack, topRight, topRight2, r, g, b, a);
        renderLine(buf, stack, bottomRight, bottomRight2, r, g, b, a);
        renderLine(buf, stack, bottomLeft, bottomLeft2, r, g, b, a);
        renderLine(buf, stack, topLeft, topLeft2, r, g, b, a);
    }

    private void renderBox(VertexConsumer buf, PoseStack.Pose stack, Vec3 topLeft, Vec3 bottomLeft, Vec3 topRight, Vec3 bottomRight, float r, float g, float b, float a) {
        renderLine(buf, stack, topLeft, bottomLeft, r, g, b, a);
        renderLine(buf, stack, topLeft, topRight, r, g, b, a);
        renderLine(buf, stack, bottomRight, bottomLeft, r, g, b, a);
        renderLine(buf, stack, bottomRight, topRight, r, g, b, a);
    }

    private void renderLine(VertexConsumer buf, PoseStack.Pose mat, Vec3 from, Vec3 to, float r, float g, float b, float a) {
        var normal = from.subtract(to);
        buf.addVertex(mat, (float) from.x, (float) from.y, (float) from.z).setColor(r, g, b, a).setNormal(mat, (float) normal.x, (float) normal.y, (float) normal.z).setLineWidth(3);
        buf.addVertex(mat, (float) to.x, (float) to.y, (float) to.z).setColor(r, g, b, a).setNormal(mat, (float) normal.x, (float) normal.y, (float) normal.z).setLineWidth(3);
    }

    @Override
    public VacuumInterfaceState createRenderState() {
        return new VacuumInterfaceState();
    }

}
