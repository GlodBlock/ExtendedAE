package com.glodblock.github.epp.common.items;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.parts.misc.InterfacePart;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.tiles.TileExInterface;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemInterfaceUpgrade extends ItemUpgrade {

    public ItemInterfaceUpgrade() {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
        this.addTile(InterfaceBlockEntity.class, EPPItemAndBlock.EX_INTERFACE, TileExInterface.TYPE);
        this.addPart(InterfacePart.class, EPPItemAndBlock.EX_INTERFACE_PART);
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("ei.upgrade.tooltip.01").formatted(Formatting.GRAY));
        list.add(Text.translatable("ei.upgrade.tooltip.02").formatted(Formatting.GRAY));
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

}
