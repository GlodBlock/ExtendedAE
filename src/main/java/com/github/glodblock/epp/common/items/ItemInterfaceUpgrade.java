package com.github.glodblock.epp.common.items;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.parts.misc.InterfacePart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemInterfaceUpgrade extends ItemUpgrade {

    public ItemInterfaceUpgrade() {
        super(new Item.Properties().tab(EPPItemAndBlock.TAB));
        this.addTile(InterfaceBlockEntity.class, EPPItemAndBlock.EX_INTERFACE, TileExInterface.class);
        this.addPart(InterfacePart.class, EPPItemAndBlock.EX_INTERFACE_PART);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        list.add(Component.translatable("ei.upgrade.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }


}
