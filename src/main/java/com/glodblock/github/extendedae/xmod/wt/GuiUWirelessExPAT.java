package com.glodblock.github.extendedae.xmod.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiUWirelessExPAT extends GuiExPatternTerminal<ContainerUWirelessExPAT> implements IUniversalTerminalCapable {

    public GuiUWirelessExPAT(ContainerUWirelessExPAT container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (this.getMenu().isWUT()) {
            this.addToLeftToolbar(new CycleTerminalButton((btn) -> this.cycleTerminal()));
        }

        this.widgets.add("upgrades", new UpgradesPanel(this.getMenu().getSlots(SlotSemantics.UPGRADE), this.getMenu().getHost()));
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }

        this.widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    public void storeState() {
    }

}
