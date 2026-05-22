package com.glodblock.github.ae2netanalyser.client.render;

import com.glodblock.github.ae2netanalyser.client.render.buffer.CachedRender;
import com.glodblock.github.ae2netanalyser.client.render.pipeline.NetworkPipelines;
import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.ae2netanalyser.common.me.ticker.ProfileData;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.DoublePredicate;

@SuppressWarnings("DuplicatedCode")
public class ProfileRender {

    public static final ProfileRender INSTANCE = new ProfileRender();
    private static ItemStack currentProfiler;
    private static final ColorData WHITE = new ColorData(1f, 1f, 1f);
    private static final float CUBE_SIZE = 0.8f;
    private static final CachedRender CR = new CachedRender(NetworkPipelines.CUBE_PIPELINE, "ME Network Profiler");

    public static void hook(RenderLevelStageEvent.AfterTranslucentParticles event) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.getMainHandItem().getItem() == AEASingletons.TICK_ANALYSER.get()) {
            if (currentProfiler != player.getMainHandItem()) {
                currentProfiler = player.getMainHandItem();
                ProfileDataHandler.updateConfig(currentProfiler.getOrDefault(AEASingletons.TICK_CONFIG, ItemTickAnalyzer.defaultConfig));
            }
            INSTANCE.tick(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.level().dimension());
        }
    }

    public void createVBO(DoublePredicate filter, ResourceKey<@NotNull Level> world, ProfileData data) {
        var buf = new BufferBuilder(new ByteBufferBuilder(NetworkPipelines.CUBE_RENDER.bufferSize() * 8), NetworkPipelines.CUBE_RENDER.mode(), NetworkPipelines.CUBE_RENDER.format());
        var stack = new PoseStack();
        for (var tick : data.ticks) {
            if (world.equals(tick.pos().dimension()) && filter.test(tick.rate())) {
                NetworkRender.INSTANCE.drawCube(CUBE_SIZE, tick.color(), tick.pos().pos(), stack, buf);
            }
        }
        CR.upload(buf);
    }

    public void tick(PoseStack stack, MultiBufferSource.BufferSource multiBuf, Camera camera, ResourceKey<@NotNull Level> world) {
        if (ProfileDataHandler.pullData() == null) {
            return;
        }
        if (camera.isInitialized()) {
            var offset = camera.position().reverse();
            var filter = ProfileDataHandler.renderFilter();
            if (ProfileDataHandler.update()) {
                createVBO(filter, world, ProfileDataHandler.pullData());
            }
            if (CR.ready()) {
                stack.pushPose();
                var rotation = new Quaternionf(camera.rotation());
                rotation.invert();
                stack.mulPose(rotation);
                stack.translate(offset.x, offset.y, offset.z);
                CR.render(stack.last().pose());
                stack.popPose();
            }
            for (var tick : ProfileDataHandler.pullData().ticks) {
                if (world.equals(tick.pos().dimension()) && filter.test(tick.rate())) {
                    NetworkRender.INSTANCE.drawInWorldText((int) tick.rate() + "μs/t", WHITE, tick.pos().pos().getCenter(), offset, camera, stack, multiBuf);
                }
            }
            multiBuf.endBatch();
        }
    }

}
