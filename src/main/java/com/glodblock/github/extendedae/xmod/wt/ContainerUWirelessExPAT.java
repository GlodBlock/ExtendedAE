package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.storage.ITerminalHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerUWirelessExPAT extends ContainerExPatternTerminal {

    public static final MenuType<ContainerUWirelessExPAT> TYPE = MenuTypeBuilder
            .create(ContainerUWirelessExPAT::new, HostUWirelessExPAT.class)
            .build(ExtendedAE.id("u_wireless_ex_pattern_access_terminal"));
    private final HostUWirelessExPAT host;
    private final ToolboxMenu toolboxMenu;

    public ContainerUWirelessExPAT(int id, Inventory ip, HostUWirelessExPAT host) {
        super(TYPE, id, ip, host, true);
        this.host = host;
        this.toolboxMenu = new ToolboxMenu(this);
        IUpgradeInventory upgrades = this.host.getUpgrades();

        for(int i = 0; i < upgrades.size(); ++i) {
            RestrictedInputSlot slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            this.addSlot(slot, SlotSemantics.UPGRADE);
        }

        this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY, this.host.getSubInventory(WCTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public void broadcastChanges() {
        this.toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return this.host.getItemStack().getItem() instanceof ItemWUT;
    }

    public ITerminalHost getHost() {
        return this.host;
    }

    public ToolboxMenu getToolbox() {
        return this.toolboxMenu;
    }

}
