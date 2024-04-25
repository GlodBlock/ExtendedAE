package com.github.glodblock.extendedae.container;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import com.github.glodblock.extendedae.common.me.itemhost.HostWirelessExPAT;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;

public class ContainerWirelessExPAT extends ContainerExPatternTerminal {

    public static final MenuType<ContainerWirelessExPAT> TYPE = MenuTypeBuilder
            .create(ContainerWirelessExPAT::new, HostWirelessExPAT.class)
            .build("wireless_ex_pat");

    protected final HostWirelessExPAT terminal;
    private final ToolboxMenu toolbox;

    public ContainerWirelessExPAT(int id, Inventory ip, HostWirelessExPAT host) {
        super(TYPE, id, ip, host, true);
        this.terminal = host;
        this.toolbox = new ToolboxMenu(this);
        this.setupUpgrades();
    }

    protected void setupUpgrades() {
        var upgrades = this.terminal.getUpgrades();
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

    public final IUpgradeInventory getUpgrades() {
        return this.terminal.getUpgrades();
    }

    public final boolean hasUpgrade(ItemLike upgradeCard) {
        return getUpgrades().isInstalled(upgradeCard);
    }

    public ToolboxMenu getToolbox() {
        return this.toolbox;
    }

}