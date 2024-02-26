package com.glodblock.github.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerCaner extends AEBaseMenu {

    public static final MenuType<ContainerCaner> TYPE = MenuTypeBuilder
            .create(ContainerCaner::new, TileCaner.class)
            .build("caner");

    public ContainerCaner(int id, Inventory playerInventory, TileCaner host) {
        super(TYPE, id, playerInventory, host);
        this.addSlot(new AppEngSlot(new ConfigMenuInventory(host.getStuff()), 0), ExSemantics.EX_1);
        this.addSlot(new AppEngSlot(host.getContainer(), 0), ExSemantics.EX_2);
        this.createPlayerInventorySlots(playerInventory);
    }

}
