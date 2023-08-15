package com.github.glodblock.epp.common.items;

import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.world.item.Item;

public class ItemIOBusUpgrade extends ItemUpgrade {

    public ItemIOBusUpgrade() {
        super(new Item.Properties());
        this.addPart(ExportBusPart.class, EPPItemAndBlock.EX_EXPORT_BUS);
        this.addPart(ImportBusPart.class, EPPItemAndBlock.EX_IMPORT_BUS);
    }

}
