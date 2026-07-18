package com.glodblock.github.extendedae.container;

import appeng.api.networking.IGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.common.me.itemhost.HostWirelessExCT;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerWirelessExCT extends ContainerExCraftingTerminal {

    public static final MenuType<ContainerWirelessExCT> TYPE = MenuTypeBuilder
            .create(ContainerWirelessExCT::new, HostWirelessExCT.class)
            .build("wireless_ex_ct");

    private final ToolboxMenu toolbox;

    public ContainerWirelessExCT(int id, Inventory playerInventory, HostWirelessExCT host) {
        super(TYPE, id, playerInventory, host, true);
        this.toolbox = new ToolboxMenu(this);
        this.setupUpgrades();
    }

    protected void setupUpgrades() {
        var upgrades = this.getHost().getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            this.addSlot(slot, SlotSemantics.UPGRADE);
        }
    }

    @Override
    public void broadcastChanges() {
        this.toolbox.tick();
        super.broadcastChanges();
    }

    @Override
    public IGridNode getNetworkNode() {
        return ((HostWirelessExCT) this.getHost()).getActionableNode();
    }

    public final IUpgradeInventory getUpgrades() {
        return this.getHost().getUpgrades();
    }

    public ToolboxMenu getToolbox() {
        return this.toolbox;
    }

}
