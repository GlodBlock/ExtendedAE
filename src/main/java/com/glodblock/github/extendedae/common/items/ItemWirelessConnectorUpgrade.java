package com.glodblock.github.extendedae.common.items;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;

public class ItemWirelessConnectorUpgrade extends ItemUpgrade {

    public ItemWirelessConnectorUpgrade(Properties props) {
        super(props);
        this.addTile(TileWirelessConnector.class, EAESingletons.WIRELESS_HUB);
    }

}
