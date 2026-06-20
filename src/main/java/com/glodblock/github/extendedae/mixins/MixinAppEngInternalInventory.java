package com.glodblock.github.extendedae.mixins;

import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.util.helper.IntruderInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(AppEngInternalInventory.class)
public class MixinAppEngInternalInventory implements IntruderInventory {

    @Final
    @Shadow(remap = false)
    private NonNullList<@NotNull ItemStack> stacks;

    @Shadow(remap = false)
    private InternalInventoryHost host;

    @Override
    public ItemStack[] getStacks() {
        return this.stacks.toArray(new ItemStack[0]);
    }

    @Override
    public void setStacks(ItemStack[] stacks) {
        for (int x = 0; x < Math.min(this.stacks.size(), stacks.length); ++x) {
            this.stacks.set(x, stacks[x].copy());
        }
        if (this.host != null) {
            this.host.saveChangedInventory((AppEngInternalInventory) (Object) this);
        }
    }

    @Override
    public void setStackSilent(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
    }

    @Override
    public void silentClear() {
        for (int x = 0; x < this.stacks.size(); ++x) {
            this.stacks.set(x, ItemStack.EMPTY);
        }
    }

}
