package com.glodblock.github.ae2netanalyser.client.render;

import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.ae2netanalyser.common.me.ticker.ProfileData;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.function.DoublePredicate;

@SuppressWarnings("DuplicatedCode")
@OnlyIn(Dist.CLIENT)
public class ProfileRender extends RenderType {

    public static final ProfileRender INSTANCE = new ProfileRender();
    private static ItemStack currentProfiler;
    private static VertexBuffer VBO = null;
    private final RenderType CUBE_RENDER = NetworkRender.INSTANCE.CUBE_RENDER;
    private static final ColorData WHITE = new ColorData(1f, 1f, 1f);
    private static final float CUBE_SIZE = 0.8f;

    public static void hook(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            var player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() == AEASingletons.TICK_ANALYSER) {
                if (currentProfiler != player.getMainHandItem()) {
                    currentProfiler = player.getMainHandItem();
                    ProfileDataHandler.updateConfig(currentProfiler.getOrDefault(AEASingletons.TICK_CONFIG, ItemTickAnalyzer.defaultConfig));
                }
                INSTANCE.tick(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), event.getProjectionMatrix(), event.getCamera(), player.level().dimension());
            }
        }
    }

    public void createVBO(DoublePredicate filter, ResourceKey<Level> world, ProfileData data) {
        if (VBO != null) {
            VBO.close();
        }
        var buf = new BufferBuilder(new ByteBufferBuilder(CUBE_RENDER.bufferSize() * 8), CUBE_RENDER.mode(), CUBE_RENDER.format());
        var stack = new PoseStack();
        for (var tick : data.ticks) {
            if (world.equals(tick.pos().dimension()) && filter.test(tick.rate())) {
                NetworkRender.INSTANCE.drawCube(CUBE_SIZE, tick.color(), tick.pos().pos(), stack, buf);
            }
        }
        var rendered = buf.build();
        if (rendered != null) {
            VBO = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            VBO.bind();
            VBO.upload(rendered);
            VertexBuffer.unbind();
        }
    }

    public void tick(PoseStack stack, MultiBufferSource.BufferSource multiBuf, Matrix4f pro, Camera camera, ResourceKey<Level> world) {
        if (ProfileDataHandler.pullData() == null || GameRenderer.getPositionColorShader() == null) {
            return;
        }
        if (camera.isInitialized()) {
            var offset = camera.getPosition().reverse();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            var filter = ProfileDataHandler.renderFilter();
            if (ProfileDataHandler.update()) {
                createVBO(filter, world, ProfileDataHandler.pullData());
            }
            if (VBO != null) {
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                RenderSystem.blendFunc(
                        GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
                RenderSystem.disableCull();
                stack.pushPose();
                var rotation = new Quaternionf(camera.rotation());
                rotation.invert();
                stack.mulPose(rotation);
                stack.translate(offset.x, offset.y, offset.z);
                VBO.bind();
                VBO.drawWithShader(
                        stack.last().pose(),
                        pro,
                        GameRenderer.getPositionColorShader()
                );
                VertexBuffer.unbind();
                stack.popPose();
                RenderSystem.enableCull();
            }
            RenderSystem.disableBlend();
            for (var tick : ProfileDataHandler.pullData().ticks) {
                if (world.equals(tick.pos().dimension()) && filter.test(tick.rate())) {
                    NetworkRender.INSTANCE.drawInWorldText((int) tick.rate() + "μs/t", WHITE, tick.pos().pos().getCenter(), offset, camera, stack, multiBuf);
                }
            }
            multiBuf.endBatch();
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
    }

    public ProfileRender() {
        super("", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 0, false, false, () -> {}, () -> {});
    }

}
