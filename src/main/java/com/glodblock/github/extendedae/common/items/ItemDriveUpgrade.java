package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import net.minecraft.world.item.Item;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade() {
        super(new Item.Properties());
        this.addTile(DriveBlockEntity.class, EAESingletons.EX_DRIVE, TileExDrive.class);
    }

}
