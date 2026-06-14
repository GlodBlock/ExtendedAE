package com.glodblock.github.appflux.common.items;

import appeng.items.parts.PartItem;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemPartFluxAccessor extends PartItem<PartFluxAccessor> {

    public ItemPartFluxAccessor(Properties properties) {
        super(properties, PartFluxAccessor.class, PartFluxAccessor::new);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext ctx, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag advancedTooltips) {
        tooltip.accept(Component.translatable("block.appflux.flux_accessor.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("block.appflux.flux_accessor.tooltip.2").withStyle(ChatFormatting.GRAY));
    }

}
