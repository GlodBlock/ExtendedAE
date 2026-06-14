package com.glodblock.github.appflux.client.render;

import appeng.client.api.AEKeyRenderer;
import appeng.client.gui.style.Blitter;
import appeng.util.Platform;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluxKeyRenderHandler implements AEKeyRenderer<FluxKey, FluxKeyRenderHandler.State> {

    public static final FluxKeyRenderHandler INSTANCE = new FluxKeyRenderHandler();

    private FluxKeyRenderHandler() {
        // NO-OP
    }

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphicsExtractor guiGraphics, int x, int y, FluxKey stack) {
        var type = stack.getEnergyType();
        Blitter.sprite(Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(type.getIcon()))
                .blending(false)
                .dest(x, y, 16, 16)
                .blit(guiGraphics);
    }

    @Override
    public Class<State> stateClass() {
        return State.class;
    }

    @Override
    public State createState() {
        return new State();
    }

    @Override
    public void extract(State state, FluxKey what, @Nullable Level level, int seed) {
        var type = what.getEnergyType();
        state.sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(type.getIcon());
    }

    @Override
    public void submit(PoseStack poseStack, State state, SubmitNodeCollector nodes, int lightCoords) {
        var sprite = state.sprite;
        var x0 = -1 / 2f;
        var y0 = 1 / 2f;
        var x1 = 1 / 2f;
        var y1 = -1 / 2f;
        var color = 0xFFFFFFFF;
        poseStack.pushPose();
        poseStack.translate(0, 0, 0.01f);
        nodes.submitCustomGeometry(poseStack, RenderTypes.entitySolid(state.sprite.atlasLocation()),  (transform, buffer) -> {
            buffer.addVertex(transform, x0, y1, 0)
                    .setColor(color)
                    .setUv(sprite.getU0(), sprite.getV1())
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightCoords)
                    .setNormal(0, 0, 1);
            buffer.addVertex(transform, x1, y1, 0)
                    .setColor(color)
                    .setUv(sprite.getU1(), sprite.getV1())
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightCoords)
                    .setNormal(0, 0, 1);
            buffer.addVertex(transform, x1, y0, 0)
                    .setColor(color)
                    .setUv(sprite.getU1(), sprite.getV0())
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightCoords)
                    .setNormal(0, 0, 1);
            buffer.addVertex(transform, x0, y0, 0)
                    .setColor(color)
                    .setUv(sprite.getU0(), sprite.getV0())
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lightCoords)
                    .setNormal(0, 0, 1);
        });
        poseStack.popPose();
    }

    @Override
    public List<Component> getTooltip(FluxKey stack) {
        var tooltip = new ArrayList<Component>();
        tooltip.add(stack.getDisplayName());
        var modName = Platform.formatModName(stack.getModId());
        if (!tooltip.getLast().getString().equals(modName)) {
            tooltip.add(Component.literal(modName));
        }
        return tooltip;
    }

    public static class State {

        TextureAtlasSprite sprite;

    }

}
