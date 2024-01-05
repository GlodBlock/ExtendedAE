package com.glodblock.github.extendedae.common.items;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.items.AEBaseItem;
import appeng.items.storage.StorageCellTooltipComponent;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.config.EPPConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InfinityCell extends AEBaseItem implements ICellWorkbenchItem {

    public InfinityCell() {
        super(new Item.Properties().stacksTo(1));
    }

    @NotNull
    public AEKey getRecord(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null) {
            var key = AEKey.fromTagGeneric(tag.getCompound("record"));
            if (key != null) {
                return key;
            }
        }
        return AEFluidKey.of(Fluids.WATER);
    }

    public ItemStack getRecordCell(AEKey record) {
        var stack = new ItemStack(EPPItemAndBlock.INFINITY_CELL);
        var tag = new CompoundTag();
        tag.put("record", record.toTagGeneric());
        stack.setTag(tag);
        return stack;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack is) {
        return Component.translatable("item.expatternprovider.infinity_cell_name", this.getRecord(is).getDisplayName());
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.Output output) {
        EPPConfig.infCellFluid.forEach(f -> output.accept(getRecordCell(AEFluidKey.of(f))));
        EPPConfig.infCellItem.forEach(i -> output.accept(getRecordCell(AEItemKey.of(i))));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        lines.add(Component.translatable("infinity.tooltip").withStyle(ChatFormatting.GREEN));
    }

    @NotNull
    @Override
    public Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        var upgrades = new ArrayList<ItemStack>();
        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            for (var upgrade : this.getUpgrades(stack)) {
                upgrades.add(upgrade);
            }
        }
        var content = Collections.singletonList(new GenericStack(this.getRecord(stack), getAsIntMax(this.getRecord(stack))));
        return Optional.of(new StorageCellTooltipComponent(upgrades, content, false, true));
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
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
        if (key instanceof AEFluidKey) {
            return (long) Integer.MAX_VALUE * AEFluidKey.AMOUNT_BUCKET;
        }
        return Integer.MAX_VALUE;
    }

}
