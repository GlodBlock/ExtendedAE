package com.glodblock.github.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.extendedae.util.SingleThreadLRU;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CraftingMatrixThread extends CraftingThread {

    private static final int COOL_TIME = 5 * 20;
    private int blockCoolDown = 0;
    private final Supplier<IActionSource> sourceGetter;
    private final SingleThreadLRU<AEItemKey, InternalInventory> cache;

    public CraftingMatrixThread(@NotNull AEBaseBlockEntity host, @NotNull Supplier<IActionSource> sourceGetter, SignalAccepter accepter, SingleThreadLRU<AEItemKey, InternalInventory> cache) {
        super(host, accepter);
        this.sourceGetter = sourceGetter;
        this.cache = cache;
    }

    @Override
    protected void fillGrid(KeyCounter[] table, IMolecularAssemblerSupportedPattern adapter) {
        if (adapter instanceof AECraftingPattern crafting) {
            if (crafting.canSubstitute) {
                adapter.fillCraftingGrid(table, this.gridInv::setItemDirect);
            } else {
                var layout = this.cache.get(crafting.getDefinition());
                if (layout != null) {
                    this.copyContents(layout);
                } else {
                    adapter.fillCraftingGrid(table, this.gridInv::setItemDirect);
                    this.cache.put(crafting.getDefinition(), this.clone(this.gridInv));
                }
            }
        } else {
            adapter.fillCraftingGrid(table, this.gridInv::setItemDirect);
        }
    }

    @Override
    protected boolean hasMats() {
        if (this.myPlan == null) {
            return false;
        }
        return !this.gridInv.isEmpty();
    }

    @Override
    protected void ejectHeldItems() {
        if (this.gridInv.getStackInSlot(9).isEmpty()) {
            for (int x = 0; x < 9; x++) {
                final ItemStack is = this.gridInv.getStackInSlot(x);
                if (!is.isEmpty()) {
                    this.gridInv.setItemDirect(9, is);
                    this.gridInv.setItemDirect(x, ItemStack.EMPTY);
                    this.saveChanges();
                    return;
                }
            }
        }
    }

    @Override
    public TickRateModulation tick(int cards, int ticksSinceLastCall) {
        if (this.blockCoolDown > 0) {
            this.blockCoolDown -= ticksSinceLastCall;
            return TickRateModulation.SAME;
        } else {
            return super.tick(cards, ticksSinceLastCall);
        }
    }

    @Override
    protected ItemStack assemblePattern(CraftingInput input) {
        if (this.myPlan instanceof AECraftingPattern crafting) {
            var recipe = Ae2Reflect.getCraftRecipe(crafting).value();
            if (crafting.canSubstitute && recipe.isSpecial()) {
                return super.assemblePattern(input);
            }
            return Ae2Reflect.getCraftRecipeResult(crafting);
        } else {
            return super.assemblePattern(input);
        }
    }

    @Override
    protected void pushOut(ItemStack output) {
        output = this.pushTo(output);
        if (!output.isEmpty()) {
            this.blockCoolDown = COOL_TIME;
        }
        if (output.isEmpty() && this.forcePlan) {
            this.forcePlan = false;
            this.recalculatePlan();
        }
        this.gridInv.setItemDirect(9, output);
    }

    private ItemStack pushTo(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        var grid = this.girdHost.getMainNode().getGrid();
        if (grid != null) {
            var storage = grid.getService(IStorageService.class);
            var added = storage.getInventory().insert(AEItemKey.of(stack), stack.getCount(), Actionable.MODULATE, this.sourceGetter.get());
            if (added == 0) {
                return stack;
            }
            this.saveChanges();
            if (added != stack.getCount()) {
                return stack.copyWithCount((int) (stack.getCount() - added));
            } else {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    private void copyContents(InternalInventory target) {
        for (var x = 0; x < 9; x++) {
            this.gridInv.setItemDirect(x, target.getStackInSlot(x).copy());
        }
    }

    private InternalInventory clone(InternalInventory inv) {
        var copy = new AppEngInternalInventory(null, 9);
        for (var x = 0; x < 9; x++) {
            copy.setItemDirect(x, inv.getStackInSlot(x).copy());
        }
        return copy;
    }


}
