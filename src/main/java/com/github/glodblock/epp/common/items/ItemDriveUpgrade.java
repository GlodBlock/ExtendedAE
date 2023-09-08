package com.github.glodblock.epp.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import net.minecraft.world.item.Item;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade() {
        super(new Item.Properties());
        this.addTile(DriveBlockEntity.class, EPPItemAndBlock.EX_DRIVE, TileExDrive.class);
    }

}
