package com.glodblock.github.extendedae.client.gui.widget.panel;

import appeng.client.Point;
import appeng.client.gui.Icon;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.gui.GuiExCraftingTerminal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CraftingPanel extends ExPanel {

    private static final Blitter BG = Blitter.texture("guis/ex_crafting_modes.png").src(0, 0, 126, 68);
    private final ActionEPPButton clearCraftingGrid;
    private final ActionEPPButton clearToPlayerInv;

    public CraftingPanel(GuiExCraftingTerminal screen, WidgetContainer widgets) {
        super(screen, widgets);
        this.clearCraftingGrid = new ActionEPPButton(b -> this.sendPacket("craft_clearCraftingGrid"), Icon.ARROW_UP);
        this.clearCraftingGrid.setHalfSize(true);
        this.clearToPlayerInv = new ActionEPPButton(b -> this.sendPacket("craft_clearToPlayerInv"), Icon.ARROW_DOWN);
        this.clearToPlayerInv.setHalfSize(true);
        this.widgets.add("clearToPlayerInv", clearCraftingGrid);
        this.widgets.add("clearCraftingGrid", clearToPlayerInv);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    public Component getTabTooltip() {
        return CraftingMode.CRAFTING.getDisplayName();
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 24, bounds.getY() + bounds.getHeight() - 164).blit(guiGraphics);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.clearCraftingGrid.setVisibility(visible);
        this.clearToPlayerInv.setVisibility(visible);
        this.screen.setSlotsHidden(SlotSemantics.CRAFTING_GRID, !visible);
        this.screen.setSlotsHidden(SlotSemantics.CRAFTING_RESULT, !visible);
    }

}
