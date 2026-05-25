package com.glodblock.github.extendedae.client.gui.pattern;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEItemKey;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;
import appeng.items.misc.WrappedGenericStack;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public abstract class GuiPattern<T extends ContainerPattern> extends AbstractContainerScreen<T> {

    private int cycle = 0;
    private int cycleTick = 0;

    public GuiPattern(T container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.menu.setCycleItem(this.cycle);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int x, int y) {
        // NO-OP
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.hoveredSlot instanceof ContainerPattern.DisplayOnlySlot dpSlot && !dpSlot.getItem().isEmpty()) {
            var stack = dpSlot.getItem();
            var item = dpSlot.getItem().getItem();
            var key = item instanceof WrappedGenericStack wgs
                    ? wgs.unwrapWhat(stack) : AEItemKey.of(stack);
            var amount = item instanceof WrappedGenericStack wgs
                    ? wgs.unwrapAmount(stack) : dpSlot.getActualAmount();
            if (key != null && amount > 0) {
                var currentToolTip = AEKeyRendering.getTooltip(key);
                if (Tooltips.shouldShowAmountTooltip(key, amount)) {
                    currentToolTip.add(Tooltips.getAmountTooltip(ButtonToolTips.StoredAmount, key, amount));
                }
                if (key instanceof AEItemKey) {
                    guiGraphics.renderTooltip(this.font, currentToolTip, stack.getTooltipImage(), stack, x, y);
                } else {
                    guiGraphics.renderComponentTooltip(this.font, currentToolTip, x, y);
                }
            }
            return;
        }
        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.cycleTick % 80 == 0) {
            this.cycle ++;
            this.menu.setCycleItem(this.cycle);
        }
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.hoveredSlot != null) {
            guiGraphics.hLine(leftPos + this.hoveredSlot.x, leftPos + this.hoveredSlot.x + 16, topPos + this.hoveredSlot.y - 1, 0xFFdaffff);
            guiGraphics.hLine(leftPos + this.hoveredSlot.x - 1, leftPos + this.hoveredSlot.x + 16, topPos + this.hoveredSlot.y + 16, 0xFFdaffff);
            guiGraphics.vLine(leftPos + this.hoveredSlot.x - 1, topPos + this.hoveredSlot.y - 2, topPos + this.hoveredSlot.y + 16, 0xFFdaffff);
            guiGraphics.vLine(leftPos + this.hoveredSlot.x + 16, topPos + this.hoveredSlot.y - 2, topPos + this.hoveredSlot.y + 16, 0xFFdaffff);
            renderSlotHighlight(guiGraphics, leftPos + this.hoveredSlot.x, topPos + this.hoveredSlot.y, 0, 0x669cd3ff);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.cycleTick ++;
    }

}
