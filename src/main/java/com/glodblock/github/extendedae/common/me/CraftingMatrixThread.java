package com.glodblock.github.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.pattern.AECraftingPattern;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.extendedae.util.IntruderInventory;
import com.glodblock.github.extendedae.util.SingleThreadLRU;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CraftingMatrixThread extends CraftingThread {

    private static final int COOL_TIME = 5 * 20;
    private static final SingleThreadLRU<AEItemKey, SearchResult> LRU = new SingleThreadLRU<>(new Object2ObjectOpenHashMap<>(100), 100);
    private int blockCoolDown = 0;
    private final Supplier<IActionSource> sourceGetter;

    public CraftingMatrixThread(@NotNull AEBaseBlockEntity host, @NotNull Supplier<IActionSource> sourceGetter, SignalAccepter accepter) {
        super(host, accepter);
        this.sourceGetter = sourceGetter;
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

    @Override
    public void fillGrid(KeyCounter[] table, IMolecularAssemblerSupportedPattern adapter) {
        if (adapter instanceof AECraftingPattern cp) {
            if (cp.canSubstitute) {
                super.fillGrid(table, cp);
            } else {
                var cache = LRU.get(cp.getDefinition());
                if (cache != null && cache.isValid()) {
                    this.fillGridFromArray(cache);
                    return;
                }
                if (cache != null) {
                    // Force to fail
                    super.fillGrid(table, cp);
                }
                LRU.put(cp.getDefinition(), this.makeResult(table, cp));
            }
        } else {
            super.fillGrid(table, adapter);
        }
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

    private SearchResult makeResult(KeyCounter[] table, AECraftingPattern pattern) {
        // Make the cache
        super.fillGrid(table, pattern);
        if (pattern.canSubstituteFluids) {
            for (var input : pattern.getInputs()) {
                for (var stack : input.getPossibleInputs()) {
                    if (stack != null && !(stack.what() instanceof AEItemKey)) {
                        // Contains mutable stacks
                        return SearchResult.NULL;
                    }
                }
            }
        }
        ItemStack[] clone = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            clone[i] = this.gridInv.getStackInSlot(i).copy();
        }
        return new SearchResult(clone);
    }

    private void fillGridFromArray(SearchResult result) {
        ((IntruderInventory) this.gridInv).setStacks(result.results);
    }

    private record SearchResult(ItemStack[] results) {

        static SearchResult NULL = new SearchResult(null);

        boolean isValid() {
            return this.results != null;
        }

    }

}
