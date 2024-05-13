package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import net.minecraft.world.item.Item;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade() {
        super(new Item.Properties());
        this.addTile(DriveBlockEntity.class, EAEItemAndBlock.EX_DRIVE, TileExDrive.class);
    }

}
