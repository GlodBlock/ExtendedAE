package com.glodblock.github.extendedae.container;

import appeng.blockentity.storage.DriveBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerExDrive extends AEBaseMenu {

    public static final MenuType<@NotNull ContainerExDrive> TYPE = MenuTypeBuilder
            .create(ContainerExDrive::new, TileExDrive.class)
            .buildUnregistered(ExtendedAE.id("ex_drive"));

    public ContainerExDrive(int id, Inventory ip, DriveBlockEntity drive) {
        super(TYPE, id, ip, drive);

        for (int i = 0; i < 10; i++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS,
                    drive.getInternalInventory(), i), SlotSemantics.STORAGE_CELL);
        }
        for (int i = 0; i < 10; i++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.STORAGE_CELLS,
                    drive.getInternalInventory(), i + 10), ExSemantics.EX_1);
        }

        this.createPlayerInventorySlots(ip);
    }

}
