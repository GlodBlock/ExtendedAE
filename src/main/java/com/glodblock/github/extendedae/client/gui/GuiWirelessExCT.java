package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import com.glodblock.github.extendedae.container.ContainerWirelessExCT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiWirelessExCT extends GuiExCraftingTerminal<ContainerWirelessExCT> {

    public GuiWirelessExCT(ContainerWirelessExCT menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        if (menu.getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, menu.getToolbox().getName()));
        }
    }

}
