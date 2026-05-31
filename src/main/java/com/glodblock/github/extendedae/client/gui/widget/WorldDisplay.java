package com.glodblock.github.extendedae.client.gui.widget;

import appeng.client.gui.AEBaseScreen;
import com.glodblock.github.extendedae.util.DisplayServerLevel;
import com.glodblock.github.glodium.util.GlodUtil;
import guideme.color.LightDarkMode;
import guideme.document.LytRect;
import guideme.extensions.ExtensionCollection;
import guideme.internal.scene.ScenePictureInPictureRenderer;
import guideme.scene.CameraSettings;
import guideme.scene.GuidebookLevelRenderer;
import guideme.scene.GuidebookScene;
import guideme.scene.LytGuidebookScene;
import guideme.scene.level.GuidebookLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

import java.util.List;

// Reuse AE guide's fake world stuff
public class WorldDisplay extends AbstractWidget {

    private final AEBaseScreen<?> addedOn;
    private float zoom = 2.0f;
    private GuidebookScene scene;
    private boolean ready;
    private final static GuidebookLevelRenderer LEVEL_RENDERER = GuidebookLevelRenderer.getInstance();
    private final static LytGuidebookScene DUMMY_SCENE = new LytGuidebookScene(ExtensionCollection.empty());
    private LytRect bounds;

    public WorldDisplay(AEBaseScreen<?> addedOn, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.ready = false;
    }

    public void unload() {
        this.ready = false;
    }

    public void locate(BlockPos blockPos) {
        // holy crap what shit i just made
        // TODO: 2023/8/25 optimize it later 
        this.zoom = 2.0f;
        this.ready = false;
        var clientWorld = Minecraft.getInstance().level;
        if (clientWorld == null) {
            return;
        }
        var block = clientWorld.getBlockState(blockPos);
        var te = clientWorld.getBlockEntity(blockPos);
        if (block.isAir() || te == null) {
            return;
        }
        this.scene = new GuidebookScene(new GuidebookLevel(), new CameraSettings());
        var wrap = DisplayServerLevel.create(this.scene.getLevel());
        var sizeX = new Vec3i(3, 1, 1);
        var sizeY = new Vec3i(1, 3, 1);
        var sizeZ = new Vec3i(1, 1, 3);
        var startX = BlockPos.ZERO.offset(0, 1, 1);
        var startY = BlockPos.ZERO.offset(1, 0, 1);
        var startZ = BlockPos.ZERO.offset(1, 1, 0);
        var tmp = new StructureTemplate();
        var settings = new StructurePlaceSettings();
        var random = new SingleThreadedRandomSource(0L);
        settings.setIgnoreEntities(true);
        try {
            tmp.fillFromWorld(clientWorld, blockPos.offset(-1, 0, 0), sizeX, false, List.of(Blocks.AIR));
            tmp.placeInWorld(wrap, startX, BlockPos.ZERO, settings, random, 0);
            tmp = new StructureTemplate();
            tmp.fillFromWorld(clientWorld, blockPos.offset(0, -1, 0), sizeY, false, List.of(Blocks.AIR));
            tmp.placeInWorld(wrap, startY, BlockPos.ZERO, settings, random, 0);
            tmp = new StructureTemplate();
            tmp.fillFromWorld(clientWorld, blockPos.offset(0, 0, -1), sizeZ, false, List.of(Blocks.AIR));
            tmp.placeInWorld(wrap, startZ, BlockPos.ZERO, settings, random, 0);
        } catch (Throwable ignored) {
            this.scene = new GuidebookScene(new GuidebookLevel(), new CameraSettings());
        }
        this.scene.getCameraSettings().setRotationCenter(this.scene.getWorldCenter());
        this.scene.getCameraSettings().setZoom(this.zoom);
        this.bounds = new LytRect(getX(), getY(), this.width, this.height);
        this.scene.getCameraSettings().setViewportSize(this.bounds.size());
        this.scene.centerScene();
        this.ready = true;
    }

    public void refreshBounds() {
        this.bounds = new LytRect(getX(), getY(), this.width, this.height);
    }

    @Override
    public void playDownSound(@NotNull SoundManager manager) {
        // NO-OP
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.ready) {
            if (isMouseOver(mouseX, mouseY)) {
                this.addedOn.setFocused(this);
            }
            var screenBounds = this.bounds.toScreenRectangle().transformMaxBounds(guiGraphics.pose());
            var scissorArea = guiGraphics.peekScissorStack();
            // Pre-apply scissor area
            screenBounds = scissorArea != null ? scissorArea.intersection(screenBounds) : screenBounds;
            if (screenBounds != null) {
                guiGraphics.submitPictureInPictureRenderState(new ScenePictureInPictureRenderer.State(
                        LightDarkMode.LIGHT_MODE,
                        new Matrix3x2f(guiGraphics.pose()),
                        bounds.x(),
                        bounds.y(),
                        bounds.right(),
                        bounds.bottom(),
                        DUMMY_SCENE,
                        screenBounds,
                        scissorArea,
                        (lightDarkMode, _, buffers) -> renderViewport(lightDarkMode, buffers)));
            }
        }
    }

    private void renderViewport(LightDarkMode lightDarkMode, MultiBufferSource.BufferSource buffers) {
        LEVEL_RENDERER.render(this.scene.getLevel(), this.scene.getCameraSettings(), buffers, List.of(), lightDarkMode);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double pDragX, double pDragY) {
        if (this.visible && this.ready && this.isMouseOver(event.x(), event.y())) {
            float dx = (float) pDragX;
            float dy = (float) pDragY;
            var camera = this.scene.getCameraSettings();
            if (event.button() == 0) {
                camera.setRotationY(camera.getRotationY() + dx);
                camera.setRotationX(camera.getRotationX() + dy);
            } else if (event.button() == 1) {
                camera.setOffsetX(camera.getOffsetX() + dx);
                camera.setOffsetY(camera.getOffsetY() - dy);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (this.visible && this.ready && this.isMouseOver(pMouseX, pMouseY)) {
            this.zoom = (float) GlodUtil.clamp(this.zoom + pScrollY / 5, 0.5, 10);
            this.scene.getCameraSettings().setZoom(this.zoom);
            return true;
        }
        return false;
    }

}
