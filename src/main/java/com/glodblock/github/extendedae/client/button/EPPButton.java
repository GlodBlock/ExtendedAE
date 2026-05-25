package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var item = this.getItemOverlay();
            Blitter blitter = getBlitterIcon();

            if (this.isHalfSize()) {
                this.width = 8;
                this.height = 8;
            }

            var yOffset = isHovered() ? 1 : 0;

            if (this.isHalfSize()) {
                if (!this.isDisableBackground()) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(getX(), getY()).zOffset(10).blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.renderItem(new ItemStack(item), getX(), getY(), 0, 20);
                } else if (blitter != null) {
                    if (!this.active) {
                        blitter.opacity(0.5f);
                    }
                    blitter.dest(getX(), getY()).zOffset(20).blit(guiGraphics);
                }
            } else {
                if (!this.isDisableBackground()) {
                    Icon bgIcon = isHovered() ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                            : isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;

                    bgIcon.getBlitter()
                            .dest(getX() - 1, getY() + yOffset, 18, 20)
                            .zOffset(2)
                            .blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.renderItem(new ItemStack(item), getX(), getY() + 1 + yOffset, 0, 3);
                } else if (blitter != null) {
                    blitter.dest(getX(), getY() + 1 + yOffset).zOffset(3).blit(guiGraphics);
                }
            }
        }
    }

}
