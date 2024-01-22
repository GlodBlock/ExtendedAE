package com.glodblock.github.ae2netanalyser.client.render;

import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.me.AnalyserMode;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.util.ClientUtil;
import com.glodblock.github.ae2netanalyser.util.Util;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class NetworkRender extends RenderType {

    public static final NetworkRender INSTANCE = new NetworkRender();
    private static final Set<AnalyserMode> renderNodeModes = EnumSet.of(AnalyserMode.NODES, AnalyserMode.FULL, AnalyserMode.NONUM);
    private static final Set<AnalyserMode> renderLinkModes = EnumSet.of(AnalyserMode.CHANNELS, AnalyserMode.FULL, AnalyserMode.NONUM, AnalyserMode.P2P);
    private static ItemStack currentAnalyser;
    private static final ColorData WHITE = new ColorData(1f, 1f, 1f);

    private final TransparencyStateShard STO = new RenderStateShard.TransparencyStateShard(
            "sto",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(
                        GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
    );
    private final RenderType CUBE_RENDER = create(
            "aea_cube",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false, false,
            CompositeState.builder()
                    .setTransparencyState(STO)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTextureState(NO_TEXTURE)
                    .createCompositeState(true)
    );

    public static void hook(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            var player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() == AEAItems.ANALYSER) {
                if (currentAnalyser != player.getMainHandItem()) {
                    currentAnalyser = player.getMainHandItem();
                    NetworkDataHandler.updateConfig(AEAItems.ANALYSER.getConfig(currentAnalyser));
                }
                var pos = AEAItems.ANALYSER.getPos(currentAnalyser);
                if (pos != null && pos.dimension().equals(player.level().dimension())) {
                    INSTANCE.tick(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getCamera());
                }
            }
        }
    }

    public void renderNodes(@NotNull NetworkData data, PoseStack stack, MultiBufferSource multiBuf, Vec3 offset) {
        for (var node : data.nodes) {
            var color = NetworkDataHandler.getColorByConfig(node.state().get());
            drawCube(NetworkDataHandler.getNodeSize(), color, node.pos(), offset, stack, multiBuf);
        }
    }

    public void renderLinks(@NotNull NetworkData data, PoseStack stack, MultiBufferSource multiBuf, Vec3 offset, boolean p2pOnly) {
        for (var link : data.links) {
            var flag = link.state().get();
            if (!p2pOnly || flag == LinkFlag.COMPRESSED) {
                var color = NetworkDataHandler.getColorByConfig(flag);
                drawLink(flag == LinkFlag.DENSE, color, link.a().pos(), link.b().pos(), offset, stack, multiBuf);
            }
        }
    }

    public void drawInWorldText(String text, ColorData color, Vec3 pos, Vec3 offset, Camera camera, PoseStack stack, MultiBufferSource multiBuf) {
        float scale = 0.027f;
        var fontRender = Minecraft.getInstance().font;
        var c = pos.add(offset);
        float stringMiddle = fontRender.width(text) / 2.0f;
        stack.pushPose();
        stack.translate(c.x, c.y, c.z);
        stack.mulPose(camera.rotation());
        stack.scale(-scale, -scale, scale);
        var mat = stack.last().pose();
        fontRender.drawInBatch(text, -stringMiddle, 0, color.toARGB(), false, mat, multiBuf, Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        stack.popPose();
    }

    public void drawCube(float size, ColorData color, BlockPos pos, Vec3 offset, PoseStack stack, MultiBufferSource multiBuf) {
        float half = size / 2f;
        var buf = multiBuf.getBuffer(CUBE_RENDER);
        var c = pos.getCenter();
        var box = new AABB(c.x - half, c.y - half, c.z - half, c.x + half, c.y + half, c.z + half);
        box = box.move(offset);
        Vec3 topRight = new Vec3(box.maxX, box.maxY, box.maxZ);
        Vec3 bottomRight = new Vec3(box.maxX, box.minY, box.maxZ);
        Vec3 bottomLeft = new Vec3(box.minX, box.minY, box.maxZ);
        Vec3 topLeft = new Vec3(box.minX, box.maxY, box.maxZ);
        Vec3 topRight2 = new Vec3(box.maxX, box.maxY, box.minZ);
        Vec3 bottomRight2 = new Vec3(box.maxX, box.minY, box.minZ);
        Vec3 bottomLeft2 = new Vec3(box.minX, box.minY, box.minZ);
        Vec3 topLeft2 = new Vec3(box.minX, box.maxY, box.minZ);
        drawSide(topRight, topLeft, bottomRight, bottomLeft, color, buf, stack);
        drawSide(topRight2, topRight, bottomRight2, bottomRight, color, buf, stack);
        drawSide(topLeft2, topRight2, bottomLeft2, bottomRight2, color, buf, stack);
        drawSide(topLeft, topLeft2, bottomLeft, bottomLeft2, color, buf, stack);
        drawSide(topLeft2, topRight2, topLeft, topRight, color, buf, stack);
        drawSide(bottomLeft2, bottomRight2, bottomLeft, bottomRight, color, buf, stack);
    }

    // Mojang's line render isn't suitable for thick line, so we use a slim cuboid and pretend it as a line
    public void drawLink(boolean isDense, ColorData color, BlockPos from, BlockPos to, Vec3 offset, PoseStack stack, MultiBufferSource multiBuf) {
        var buf = multiBuf.getBuffer(CUBE_RENDER);
        var a = from.getCenter().add(offset);
        var b = to.getCenter().add(offset);
        var wide = isDense ? 0.1 : 0.025;
        var law = ClientUtil.getLawVec(a, b).scale(wide);
        var law2 = ClientUtil.getLawVec2(a, b).scale(wide);
        Vec3 topRight = a.add(law2);
        Vec3 bottomRight = a.subtract(law);
        Vec3 bottomLeft = a.subtract(law2);
        Vec3 topLeft = a.add(law);
        Vec3 topRight2 = b.add(law2);
        Vec3 bottomRight2 = b.subtract(law);
        Vec3 bottomLeft2 = b.subtract(law2);
        Vec3 topLeft2 = b.add(law);
        drawSide(topRight, topLeft, bottomRight, bottomLeft, color, buf, stack);
        drawSide(topRight2, topRight, bottomRight2, bottomRight, color, buf, stack);
        drawSide(topLeft2, topRight2, bottomLeft2, bottomRight2, color, buf, stack);
        drawSide(topLeft, topLeft2, bottomLeft, bottomLeft2, color, buf, stack);
        drawSide(topLeft2, topRight2, topLeft, topRight, color, buf, stack);
        drawSide(bottomLeft2, bottomRight2, bottomLeft, bottomRight, color, buf, stack);
    }

    private void drawSide(Vec3 tr, Vec3 tl, Vec3 br, Vec3 bl, ColorData color, VertexConsumer buf, PoseStack pose) {
        var mat = pose.last().pose();
        buf.vertex(mat, (float) tr.x, (float) tr.y, (float) tr.z).color(color.getRf(), color.getGf(), color.getBf(), color.getAf()).endVertex();
        buf.vertex(mat, (float) br.x, (float) br.y, (float) br.z).color(color.getRf(), color.getGf(), color.getBf(), color.getAf()).endVertex();
        buf.vertex(mat, (float) bl.x, (float) bl.y, (float) bl.z).color(color.getRf(), color.getGf(), color.getBf(), color.getAf()).endVertex();
        buf.vertex(mat, (float) tl.x, (float) tl.y, (float) tl.z).color(color.getRf(), color.getGf(), color.getBf(), color.getAf()).endVertex();
    }

    public void tick(PoseStack stack, MultiBufferSource.BufferSource multiBuf, Camera camera) {
        if (NetworkDataHandler.pullData() == null) {
            return;
        }
        if (camera.isInitialized()) {
            var offset = camera.getPosition().reverse();
            var mode = NetworkDataHandler.getMode();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );
            stack.pushPose();
            if (renderNodeModes.contains(mode)) {
                renderNodes(NetworkDataHandler.pullData(), stack, multiBuf, offset);
            }
            if (renderLinkModes.contains(mode)) {
                renderLinks(NetworkDataHandler.pullData(), stack, multiBuf, offset, mode == AnalyserMode.P2P);
            }
            if (mode == AnalyserMode.FULL && !Util.isInfChannel()) {
                for (var link : NetworkDataHandler.pullData().links) {
                    if (link.channel() > 0) {
                        drawInWorldText(String.valueOf(link.channel()), WHITE, ClientUtil.getCenter(link.a().pos(), link.b().pos()), offset, camera, stack, multiBuf);
                    }
                }
            }
            multiBuf.endBatch();
            stack.popPose();
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    public NetworkRender() {
        super("", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 0, false, false, () -> {}, () -> {});
    }
}
