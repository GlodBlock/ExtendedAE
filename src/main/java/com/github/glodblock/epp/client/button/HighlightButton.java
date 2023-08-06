package com.github.glodblock.epp.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import com.github.glodblock.epp.client.gui.GuiExPatternTerminal;
import com.github.glodblock.epp.client.render.HighlightHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HighlightButton extends IconButton {

    private final GuiExPatternTerminal.PatternProviderInfo container;
    private final float multiplier;
    private final Player player;

    public HighlightButton(GuiExPatternTerminal.PatternProviderInfo container, float dis, Player player) {
        super(HighlightButton::press);
        this.container = container;
        this.multiplier = Math.max(1f, dis);
        this.player = player;
    }

    @Override
    protected Icon getIcon() {
        return null;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {

        if (this.visible) {
            Blitter blitter = EPPIcon.HIGHLIGHT_BLOCK;
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
            }

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();

            if (isFocused()) {
                guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
            }

            if (this.isHalfSize()) {
                var pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(getX(), getY(), 0.0F);
                pose.scale(0.5f, 0.5f, 1.f);
                if (!isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(0, 0).blit(guiGraphics);
                }
                blitter.dest(0, 0).blit(guiGraphics);
                pose.popPose();
            } else {
                if (!isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(getX(), getY()).blit(guiGraphics);
                }
                blitter.dest(getX(), getY()).blit(guiGraphics);
            }
            RenderSystem.enableDepthTest();

            var item = this.getItemOverlay();
            if (item != null) {
                guiGraphics.renderItem(new ItemStack(item), getX(), getY());
            }
        }
    }

    public static void press(Button button) {
        if (button instanceof HighlightButton btn) {
            var c = btn.container;
            HighlightHandler.highlight(c.pos(), c.playerWorld(), System.currentTimeMillis() + (long) (800 * btn.multiplier));
            var lp = (LocalPlayer) btn.player;
            if (c.pos() != null && c.playerWorld() != null) {
                lp.displayClientMessage(Component.translatable("chat.ex_pattern_access_terminal.pos", c.pos().toShortString(), c.playerWorld().location().getPath()), false);
            }
        }
    }
}
