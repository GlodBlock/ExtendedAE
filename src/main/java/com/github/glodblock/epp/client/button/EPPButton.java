package com.github.glodblock.epp.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public abstract class EPPButton extends IconButton {

    public EPPButton(OnPress onPress) {
        super(onPress);
    }

    abstract Blitter getBlitterIcon();

    @Override
    protected final Icon getIcon() {
        return null;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Blitter blitter = getBlitterIcon();
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
                fill(poseStack, this.x - 1, this.y - 1, this.x + width + 1, this.y + height + 1, 0xFFFFFFFF);
            }

            if (this.isHalfSize()) {
                poseStack.pushPose();
                poseStack.translate(this.x, this.y, 0.0F);
                poseStack.scale(0.5f, 0.5f, 1.f);
                if (!isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(0, 0).blit(poseStack, getBlitOffset());
                }
                blitter.dest(0, 0).blit(poseStack, getBlitOffset());
                poseStack.popPose();
            } else {
                if (!isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(x, y).blit(poseStack, getBlitOffset());
                }
                blitter.dest(x, y).blit(poseStack, getBlitOffset());
            }
            RenderSystem.enableDepthTest();

            var item = this.getItemOverlay();
            if (item != null) {
                Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), x, y);
            }
        }
    }

}
