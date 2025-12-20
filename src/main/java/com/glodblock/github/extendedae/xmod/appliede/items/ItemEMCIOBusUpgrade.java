package com.glodblock.github.extendedae.xmod.appliede.items;

import com.glodblock.github.extendedae.common.items.ItemUpgrade;
import com.glodblock.github.extendedae.xmod.appliede.APESingletons;
import gripe._90.appliede.part.EMCExportBusPart;
import gripe._90.appliede.part.EMCImportBusPart;

public class ItemEMCIOBusUpgrade extends ItemUpgrade {

    public ItemEMCIOBusUpgrade() {
        super(new Properties());
        this.addPart(EMCExportBusPart.class, APESingletons.EX_EMC_EXPORT_BUS);
        this.addPart(EMCImportBusPart.class, APESingletons.EX_EMC_IMPORT_BUS);
    }

}
