package com.glodblock.github.extendedae.common.items;

import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import net.minecraft.world.item.Item;

public class ItemIOBusUpgrade extends ItemUpgrade {

    public ItemIOBusUpgrade() {
        super(new Item.Properties());
        this.addPart(ExportBusPart.class, EAEItemAndBlock.EX_EXPORT_BUS);
        this.addPart(ImportBusPart.class, EAEItemAndBlock.EX_IMPORT_BUS);
    }

}
