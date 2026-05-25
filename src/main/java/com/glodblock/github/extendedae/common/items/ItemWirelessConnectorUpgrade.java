package com.glodblock.github.extendedae.common.items;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;

public class ItemWirelessConnectorUpgrade extends ItemUpgrade {

    public ItemWirelessConnectorUpgrade() {
        super(new Properties());
        this.addTile(TileWirelessConnector.class, EAESingletons.WIRELESS_HUB, TileWirelessHub.class);
    }

}
