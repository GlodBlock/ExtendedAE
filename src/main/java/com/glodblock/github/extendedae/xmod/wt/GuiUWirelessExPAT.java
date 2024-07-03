package com.glodblock.github.extendedae.xmod.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiUWirelessExPAT extends GuiExPatternTerminal<ContainerUWirelessExPAT> implements IUniversalTerminalCapable {

    public GuiUWirelessExPAT(ContainerUWirelessExPAT container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (this.getMenu().isWUT()) {
            this.addToLeftToolbar(this.cycleTerminalButton());
        }

        this.widgets.add("upgrades", new UpgradesPanel(this.getMenu().getSlots(SlotSemantics.UPGRADE), this.getMenu().getHost()));
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }

        this.widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.getMenu().getHost();
    }

    @Override
    public void storeState() {
        // NO-OP
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int keyPressed) {
        boolean value = super.keyPressed(keyCode, scanCode, keyPressed);
        return value || this.checkForTerminalKeys(keyCode, scanCode);
    }

}
