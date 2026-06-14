package com.glodblock.github.appflux.common.items;

import appeng.items.materials.UpgradeCardItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemInductionCard extends UpgradeCardItem {

    public ItemInductionCard(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext level, TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag advancedTooltips) {
        tooltip.accept(Component.translatable("item.appflux.induction_card.tooltip").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltipDisplay, tooltip, advancedTooltips);
    }

}
