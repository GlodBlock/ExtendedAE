package com.github.glodblock.epp.common.items;

import appeng.parts.reporting.PatternAccessTerminalPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPatternAccessTerminalUpgrade extends ItemUpgrade {

    public ItemPatternAccessTerminalUpgrade() {
        super(new Item.Properties());
        this.addPart(PatternAccessTerminalPart.class, EPPItemAndBlock.EX_PATTERN_TERMINAL);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        list.add(Component.translatable("epa.upgrade.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }

}
