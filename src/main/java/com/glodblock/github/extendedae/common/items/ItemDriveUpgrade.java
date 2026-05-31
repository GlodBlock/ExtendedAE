package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.glodblock.github.extendedae.common.EAESingletons;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade(Properties props) {
        super(props);
        this.addTile(DriveBlockEntity.class, EAESingletons.EX_DRIVE);
    }

}
