package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.IconButton;
import appeng.util.Icon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
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
                    Blitter.icon(Icon.TOOLBAR_BUTTON_BACKGROUND).dest(getX(), getY()).blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.item(new ItemStack(item), getX(), getY());
                } else if (blitter != null) {
                    if (!this.active) {
                        blitter.opacity(0.5f);
                    }
                    blitter.dest(getX(), getY()).blit(guiGraphics);
                }
            } else {
                if (!this.isDisableBackground()) {
                    Icon bgIcon = isHovered() ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER : isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;
                    Blitter.icon(bgIcon).dest(getX() - 1, getY() + yOffset, 18, 20).blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.item(new ItemStack(item), getX(), getY() + 1 + yOffset);
                } else if (blitter != null) {
                    blitter.dest(getX(), getY() + 1 + yOffset).blit(guiGraphics);
                }
            }
        }
    }

}
