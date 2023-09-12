package com.github.glodblock.epp.common.items;

import appeng.blockentity.storage.DriveBlockEntity;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDriveUpgrade extends ItemUpgrade {

    public ItemDriveUpgrade() {
        super(new Item.Properties().tab(EPPItemAndBlock.TAB));
        this.addTile(DriveBlockEntity.class, EPPItemAndBlock.EX_DRIVE, TileExDrive.class);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        list.add(Component.translatable("ed.upgrade.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }

}
