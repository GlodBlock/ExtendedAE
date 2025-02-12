package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class OutputButton extends EPPButton {

    private static final Component tooltipOn = Component.translatable("gui.extendedae.set_output_sides.on");
    private static final Component tooltipOff = Component.translatable("gui.extendedae.set_output_sides.off");
    private ItemStack display = ItemStack.EMPTY;
    private boolean isOn = false;

    public OutputButton(OnPress onPress) {
        super(onPress);
    }

    public void setDisplay(ItemLike stack) {
        this.display = new ItemStack(stack);
    }

    public void setOn(boolean value) {
        this.isOn = value;
    }

    public boolean isOn() {
        return this.isOn;
    }

    public void flip() {
        this.isOn ^= true;
    }

    @Override
    public @NotNull Component getMessage() {
        return this.isOn ? tooltipOn : tooltipOff;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var yOffset = isHovered() ? 1 : 0;
            Icon bgIcon = isHovered() ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                    : isOn() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;
            bgIcon.getBlitter()
                    .dest(getX() - 1, getY() + yOffset, 18, 20)
                    .zOffset(2)
                    .blit(guiGraphics);
            if (!this.display.isEmpty()) {
                guiGraphics.renderItem(this.display, getX(), getY() + 1 + yOffset, 0, 3);
            }
        }
    }

    @Override
    public Item getItemOverlay() {
        return this.display.getItem();
    }

    @Override
    Blitter getBlitterIcon() {
        return null;
    }

}
