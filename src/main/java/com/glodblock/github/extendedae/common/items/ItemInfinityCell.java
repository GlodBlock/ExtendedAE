package com.glodblock.github.extendedae.common.items;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemInfinityCell extends AEBaseItem implements ICellWorkbenchItem {

    private final AEKey record;

    public ItemInfinityCell(@NotNull AEKey type) {
        super(new Item.Properties().stacksTo(1));
        this.record = type;
    }

    public AEKey getRecord() {
        return this.record;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack is) {
        return Component.translatable("item.extendedae.infinity_cell_name", this.record.getDisplayName());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Item.@NotNull TooltipContext ctx, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        lines.add(Component.translatable("infinity.tooltip").withStyle(ChatFormatting.GREEN));
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        var content = Collections.singletonList(new GenericStack(this.record, getAsIntMax(this.record)));
        return Optional.of(new StorageCellTooltipComponent(List.of(), content, false, true));
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return FuzzyMode.IGNORE_ALL;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {
        // NO-OP
    }

    public static long getAsIntMax(AEKey key) {
        return (long) Integer.MAX_VALUE * key.getAmountPerUnit();
    }

}
