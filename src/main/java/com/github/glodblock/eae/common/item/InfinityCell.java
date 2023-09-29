package com.github.glodblock.eae.common.item;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.fluids.util.AEFluidStack;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.item.AEItemStack;
import com.github.glodblock.eae.common.EAEConfig;
import com.github.glodblock.eae.common.EAEItemAndBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class InfinityCell extends AEBaseItem implements ICellWorkbenchItem {

    public InfinityCell() {
        this.setMaxStackSize(1);
    }

    public Object getRecord(ItemStack stack) {
        if (stack.getItem() instanceof InfinityCell) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                return null;
            }
            String type = tag.getString("t");
            if (type.equals("f")) {
                return AEFluidStack.fromNBT(tag.getCompoundTag("r"));
            } else if (type.equals("i")) {
                return AEItemStack.fromNBT(tag.getCompoundTag("r"));
            }
        }
        return null;
    }

    public ItemStack getRecordCell(IAEStack<?> record) {
        if (record == null) {
            return null;
        }
        record.setStackSize(1);
        NBTTagCompound tag = new NBTTagCompound();
        ItemStack cell = new ItemStack(EAEItemAndBlock.INFINITY_CELL);
        NBTTagCompound recordTag = new NBTTagCompound();
        record.writeToNBT(recordTag);
        tag.setTag("r", recordTag);
        cell.setTagCompound(tag);
        if (record instanceof IAEItemStack) {
            tag.setString("t", "i");
            return cell;
        }
        if (record instanceof IAEFluidStack) {
            tag.setString("t", "f");
            return cell;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Object record = this.getRecord(stack);
        if (record instanceof IAEItemStack) {
            return I18n.translateToLocalFormatted(getTranslationKey(stack) + ".name", ((IAEItemStack) record).getDefinition().getDisplayName());
        }
        if (record instanceof IAEFluidStack) {
            return I18n.translateToLocalFormatted(getTranslationKey(stack) + ".name", ((IAEFluidStack) record).getFluidStack().getLocalizedName());
        }
        return I18n.translateToLocal("cell.unknown");
    }

    @Override
    protected void getCheckedSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> itemStacks) {
        for (String s : EAEConfig.infItem) {
            ResourceLocation rl = new ResourceLocation(s);
            Item item = ForgeRegistries.ITEMS.getValue(rl);
            if (item != null) {
                itemStacks.add(this.getRecordCell(AEItemStack.fromItemStack(new ItemStack(item))));
            }
        }
        for (String s : EAEConfig.infFluid) {
            Fluid fluid = FluidRegistry.getFluid(s.toLowerCase());
            if (fluid != null) {
                itemStacks.add(this.getRecordCell(AEFluidStack.fromFluidStack(new FluidStack(fluid, 1))));
            }
        }
    }

    @Override
    public boolean isEditable(ItemStack itemStack) {
        return false;
    }

    @Override
    public IItemHandler getUpgradesInventory(ItemStack itemStack) {
        return null;
    }

    @Override
    public IItemHandler getConfigInventory(ItemStack itemStack) {
        return new CellConfig(itemStack);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack itemStack) {
        return FuzzyMode.IGNORE_ALL;
    }

    @Override
    public void setFuzzyMode(ItemStack itemStack, FuzzyMode fuzzyMode) {

    }
}
