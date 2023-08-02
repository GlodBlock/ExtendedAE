package com.glodblock.github.epp.common.items;

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
import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InfinityCell extends AEBaseItem implements ICellWorkbenchItem {

    public InfinityCell() {
        super(new Item.Settings().maxCount(1).group(EPPItemAndBlock.TAB));
    }

    @NotNull
    public AEKey getRecord(ItemStack stack) {
        var tag = stack.getNbt();
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
        var tag = new NbtCompound();
        tag.put("record", record.toTagGeneric());
        stack.setNbt(tag);
        return stack;
    }

    @Override
    public @NotNull Text getName(@NotNull ItemStack is) {
        return Text.translatable("item.expatternprovider.infinity_cell_name", this.getRecord(is).getDisplayName());
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(getRecordCell(AEFluidKey.of(Fluids.WATER)));
            stacks.add(getRecordCell(AEItemKey.of(Items.COBBLESTONE)));
        }
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("infinity.tooltip").formatted(Formatting.GREEN));
    }

    @NotNull
    @Override
    public Optional<TooltipData> getTooltipData(@NotNull ItemStack stack) {
        var upgrades = new ArrayList<ItemStack>();
        if (AEConfig.instance().isTooltipShowCellUpgrades()) {
            for (var upgrade : this.getUpgrades(stack)) {
                upgrades.add(upgrade);
            }
        }
        var content = Collections.singletonList(new GenericStack(this.getRecord(stack), getInfAmount(this.getRecord(stack))));
        return Optional.of(new StorageCellTooltipComponent(upgrades, content, false));
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

    public static long getInfAmount(AEKey type) {
        if (type instanceof AEFluidKey) {
            // fabric is trolling me here
            return ((long) Integer.MAX_VALUE) * AEFluidKey.AMOUNT_BUCKET;
        }
        return Integer.MAX_VALUE;
    }

}
