package com.glodblock.github.extendedae.common.items;

import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import com.glodblock.github.extendedae.common.EAESingletons;

public class ItemIOBusUpgrade extends ItemUpgrade {

    public ItemIOBusUpgrade(Properties props) {
        super(props);
        this.addPart(ExportBusPart.class, EAESingletons.EX_EXPORT_BUS);
        this.addPart(ImportBusPart.class, EAESingletons.EX_IMPORT_BUS);
    }

}
