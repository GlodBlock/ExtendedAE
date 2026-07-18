package com.glodblock.github.extendedae.client.gui.widget.panel;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.gui.GuiExCraftingTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnvilPanel extends ExPanel {

    private static final Blitter BG = Blitter.texture("guis/ex_crafting_modes.png").src(0, 70, 126, 68);
    private static final Blitter FAIL = Blitter.texture("guis/ex_crafting_modes.png").src(234, 15, 22, 15);

    private final AETextField rename;

    public AnvilPanel(GuiExCraftingTerminal<?> screen, WidgetContainer widgets) {
        super(screen, widgets);
        this.rename = widgets.addTextField("rename_input");
        this.rename.setMaxLength(50);
        this.rename.setResponder(s -> this.sendPacket("anvil_rename", s));
        this.rename.setValue(this.menu.itemName);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 24, bounds.getY() + bounds.getHeight() - 164).blit(guiGraphics);
        if (!this.menu.isAnvilRecipeValid()) {
            if (!this.menu.isInputEmpty()) {
                FAIL.dest(bounds.getX() + 97, bounds.getY() + bounds.getHeight() - 131).blit(guiGraphics);
            }
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        if (this.menu.isAnvilRecipeValid()) {
            var cost = this.menu.anvilCost;
            if (cost > 0) {
                var display = Component.translatable("container.repair.cost", cost);
                var font = Minecraft.getInstance().font;
                int k = bounds.getX() + bounds.getWidth() - font.width(display) - 50;
                guiGraphics.fill(k - 2, bounds.getY() + bounds.getHeight() - 112, bounds.getX() + bounds.getWidth() - 48, bounds.getY() + bounds.getHeight() - 100, 1325400064);
                guiGraphics.drawString(font, display, k, bounds.getY() + bounds.getHeight() - 110, 8453920);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.rename.setVisible(visible);
        this.screen.setSlotsHidden(ExSemantics.EX_2, !visible);
        this.screen.setSlotsHidden(ExSemantics.EX_3, !visible);
        this.screen.setSlotsHidden(ExSemantics.EX_4, !visible);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.ANVIL);
    }

    @Override
    public Component getTabTooltip() {
        return CraftingMode.ANVIL.getDisplayName();
    }
}
