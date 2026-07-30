package com.glodblock.github.extendedae.xmod.wt;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiWirelessExPAT extends GuiExPatternTerminal<ContainerWirelessExPAT> implements IUniversalTerminalCapable {

    private final ScrollingUpgradesPanel upgradesPanel;

    public GuiWirelessExPAT(ContainerWirelessExPAT container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (this.getMenu().isWUT()) {
            this.addTerminalSelectionPanel(this.widgets);
        }
        this.upgradesPanel = this.addUpgradePanel(this.widgets, this.getMenu());
        if (this.getMenu().getToolbox().isPresent()) {
            this.widgets.add("toolbox", new ToolboxPanel(style, this.getMenu().getToolbox().getName()));
        }
    }

    @Override
    public void init() {
        super.init();
        this.upgradesPanel.setMaxRows(Math.max(2, this.visibleRows));
    }

    @Override
    public @NotNull WTMenuHost getHost() {
        return (WTMenuHost) this.getMenu().getHost();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        boolean value = super.keyPressed(event);
        return value || this.checkForTerminalKeys(event);
    }

}
