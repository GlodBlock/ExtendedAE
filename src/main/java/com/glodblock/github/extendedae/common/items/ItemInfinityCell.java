package com.glodblock.github.extendedae.common.items;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.config.EAEConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemInfinityCell extends AEBaseItem implements ICellWorkbenchItem {

    public ItemInfinityCell() {
        super(new Item.Properties().stacksTo(1));
    }

    public ItemStack getRecordCell(AEKey record) {
        var stack = new ItemStack(EAESingletons.INFINITY_CELL);
        stack.set(EAESingletons.AE_KEY, record);
        return stack;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack is) {
        return Component.translatable("item.extendedae.infinity_cell_name", is.getOrDefault(EAESingletons.AE_KEY, AEFluidKey.of(Fluids.WATER)).getDisplayName());
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        EAEConfig.infCellFluid.forEach(f -> output.accept(getRecordCell(AEFluidKey.of(f))));
        EAEConfig.infCellItem.forEach(i -> output.accept(getRecordCell(AEItemKey.of(i))));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Item.@NotNull TooltipContext ctx, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        lines.add(Component.translatable("infinity.tooltip").withStyle(ChatFormatting.GREEN));
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        var key = stack.getOrDefault(EAESingletons.AE_KEY, AEFluidKey.of(Fluids.WATER));
        var content = Collections.singletonList(new GenericStack(key, getAsIntMax(key)));
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
