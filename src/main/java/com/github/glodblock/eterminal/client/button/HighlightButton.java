package com.github.glodblock.eterminal.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import com.github.glodblock.eterminal.client.gui.GuiExPatternTerminal;
import com.github.glodblock.eterminal.client.render.HighlightHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HighlightButton extends IconButton {

    private final GuiExPatternTerminal.PatternProviderInfo container;
    private final float multiplier;
    private final Player player;

    public HighlightButton(GuiExPatternTerminal.PatternProviderInfo container, float dis, Player player) {
        super(HighlightButton::press);
        this.container = container;
        this.multiplier = Math.min(Math.max(1f, dis), 25f);
        this.player = player;
    }

    @Override
    protected Icon getIcon() {
        return null;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {

        if (this.visible) {
            Blitter blitter = ExIcon.HIGHLIGHT_BLOCK;
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
            }

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            if (this.isFocused()) {
                fill(poseStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -1);
            }

            if (this.isHalfSize()) {
                poseStack.pushPose();
                poseStack.translate((double)this.x, (double)this.y, 0.0);
                poseStack.scale(0.5F, 0.5F, 1.0F);
                if (!this.isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(0, 0).blit(poseStack, this.getBlitOffset());
                }

                blitter.dest(0, 0).blit(poseStack, this.getBlitOffset());
                poseStack.popPose();
            } else {
                if (!this.isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(this.x, this.y).blit(poseStack, this.getBlitOffset());
                }

                blitter.dest(this.x, this.y).blit(poseStack, this.getBlitOffset());
            }

            RenderSystem.enableDepthTest();
            Item item = this.getItemOverlay();
            if (item != null) {
                Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), this.x, this.y);
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
