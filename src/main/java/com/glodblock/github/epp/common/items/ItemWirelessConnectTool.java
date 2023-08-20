package com.glodblock.github.epp.common.items;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemWirelessConnectTool extends Item {

    public ItemWirelessConnectTool() {
        super(new Item.Settings().maxCount(1).group(EPPItemAndBlock.TAB));
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        var nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        assert nbt != null;
        var freq = nbt.getLong("freq");
        var pos = BlockPos.fromLong(nbt.getLong("bind"));
        if (freq != 0) {
            list.add(Text.translatable("wireless.tooltip", pos.getX(), pos.getY(), pos.getZ()).formatted(Formatting.GRAY));
            list.add(Text.translatable("wireless.use.tooltip.02").formatted(Formatting.GRAY));
        } else {
            list.add(Text.translatable("wireless.use.tooltip.01").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

}
