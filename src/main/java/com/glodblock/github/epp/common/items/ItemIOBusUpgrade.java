package com.glodblock.github.epp.common.items;

import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemIOBusUpgrade extends ItemUpgrade {

    public ItemIOBusUpgrade() {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
        this.addPart(ExportBusPart.class, EPPItemAndBlock.EX_EXPORT_BUS);
        this.addPart(ImportBusPart.class, EPPItemAndBlock.EX_IMPORT_BUS);
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("ebus.upgrade.tooltip.01").formatted(Formatting.GRAY));
        list.add(Text.translatable("ebus.upgrade.tooltip.02").formatted(Formatting.GRAY));
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

}
