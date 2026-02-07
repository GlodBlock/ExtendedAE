package com.glodblock.github.extendedae.client.gui.widget.panel;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.gui.GuiExCraftingTerminal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmithingPanel extends ExPanel {

    private static final Blitter BG = Blitter.texture("guis/ex_crafting_modes.png").src(128, 70, 126, 68);

    public SmithingPanel(GuiExCraftingTerminal screen, WidgetContainer widgets) {
        super(screen, widgets);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 24, bounds.getY() + bounds.getHeight() - 164).blit(guiGraphics);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.screen.setSlotsHidden(SlotSemantics.SMITHING_TABLE_TEMPLATE, !visible);
        this.screen.setSlotsHidden(SlotSemantics.SMITHING_TABLE_BASE, !visible);
        this.screen.setSlotsHidden(SlotSemantics.SMITHING_TABLE_ADDITION, !visible);
        this.screen.setSlotsHidden(SlotSemantics.SMITHING_TABLE_RESULT, !visible);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.SMITHING_TABLE);
    }

    @Override
    public Component getTabTooltip() {
        return CraftingMode.SMITHING.getDisplayName();
    }

}
