package com.github.glodblock.extendedae.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.common.tileentities.TileExDrive;
import net.minecraft.world.item.Item;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade() {
        super(new Item.Properties());
        this.addTile(DriveBlockEntity.class, EAEItemAndBlock.EX_DRIVE, TileExDrive.class);
    }

}
