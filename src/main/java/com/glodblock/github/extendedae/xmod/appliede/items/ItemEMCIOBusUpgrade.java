package com.glodblock.github.extendedae.xmod.appliede.items;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.items.ItemUpgrade;
import gripe._90.appliede.part.EMCExportBusPart;
import gripe._90.appliede.part.EMCImportBusPart;
import net.minecraft.world.item.Item;

public class ItemEMCIOBusUpgrade extends ItemUpgrade {

    public ItemEMCIOBusUpgrade() {
        super(new Item.Properties());
        this.addPart(EMCExportBusPart.class, EPPItemAndBlock.EX_EMC_EXPORT_BUS);
        this.addPart(EMCImportBusPart.class, EPPItemAndBlock.EX_EMC_IMPORT_BUS);
    }

}
