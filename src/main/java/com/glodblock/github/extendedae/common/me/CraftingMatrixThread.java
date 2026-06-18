package com.glodblock.github.extendedae.common.me;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.items.misc.WrappedGenericStack;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.extendedae.util.helper.CraftingRecipePattern;
import com.glodblock.github.extendedae.util.helper.IntruderInventory;
import com.glodblock.github.extendedae.util.SingleThreadLRU;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CraftingMatrixThread extends CraftingThread {

    private static final SingleThreadLRU<AEItemKey, SearchResult> LRU = new SingleThreadLRU<>(new Object2ObjectOpenHashMap<>(100), 100);
    private final BiConsumer<AEKey, Long> pusher;

    public CraftingMatrixThread(@NotNull AEBaseBlockEntity host, @NotNull Supplier<IActionSource> sourceGetter, SignalAccepter accepter) {
        super(host, accepter);
        this.pusher = ((TileAssemblerMatrixCrafter) host)::addToBuffer;
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
        boolean changed = false;
        for (int x = 0; x < 10; x ++) {
            var stack = this.gridInv.getStackInSlot(x);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof WrappedGenericStack) {
                    var generic = GenericStack.unwrapItemStack(stack);
                    if (generic != null) {
                        this.pusher.accept(generic.what(), generic.amount());
                    }
                } else {
                    this.pusher.accept(AEItemKey.of(stack), (long) stack.getCount());
                }
                changed = true;
                // The buffer can hold anything
                this.gridInv.setItemDirect(x, ItemStack.EMPTY);
            }
        }
        if (changed) {
            this.saveChanges();
        }
    }

    @Override
    protected TickRateModulation onCraftingDone() {
        if (this.myPlan instanceof AECraftingPattern cp) {
            if (cp.canSubstitute) {
                return super.onCraftingDone();
            } else {
                var cache = LRU.get(cp.getDefinition());
                if (cache != null && cache.isValid()) {
                    ((IntruderInventory) this.gridInv).silentClear();
                    this.output = cache.output;
                    if (this.host.getLevel() != null) {
                        this.output.onCraftedBySystem(this.host.getLevel());
                    }
                    this.pusher.accept(cache.outputKey, (long) cache.amount);
                    for (var stack : cache.remains) {
                        this.pusher.accept(stack.what(), stack.amount());
                    }
                    this.reset();
                    return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
                } else {
                    return super.onCraftingDone();
                }
            }
        } else {
            return super.onCraftingDone();
        }
    }

    @Override
    protected ItemStack assemblePattern(CraftingInput input) {
        if (this.myPlan instanceof AECraftingPattern crafting) {
            var recipe = Ae2Reflect.getCraftRecipe(crafting).value();
            if (crafting.canSubstitute && recipe.isSpecial()) {
                return super.assemblePattern(input);
            }
            return ((CraftingRecipePattern) crafting).getOutput();
        }
        return super.assemblePattern(input);
    }

    @Override
    protected void pushOut(ItemStack output) {
        if (!output.isEmpty()) {
            this.pusher.accept(AEItemKey.of(output), (long) output.getCount());
        }
        if (this.forcePlan) {
            this.forcePlan = false;
            this.recalculatePlan();
        }
        this.gridInv.setItemDirect(9, ItemStack.EMPTY);
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
                    return;
                }
                LRU.put(cp.getDefinition(), this.makeResult(table, cp));
            }
        } else {
            super.fillGrid(table, adapter);
        }
    }

    private SearchResult makeResult(KeyCounter[] table, AECraftingPattern pattern) {
        // Make the cache
        super.fillGrid(table, pattern);
        ItemStack[] inputClone = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            inputClone[i] = this.gridInv.getStackInSlot(i).copy();
        }
        var outputs = this.getPatternOutput(pattern);
        if (outputs == null) {
            return SearchResult.NULL;
        }
        return new SearchResult(inputClone, outputs.first(), outputs.second(), AEItemKey.of(outputs.second()), outputs.second().getCount());
    }

    private Pair<List<GenericStack>, ItemStack> getPatternOutput(AECraftingPattern pattern) {
        for (int x = 0; x < this.craftingInv.getContainerSize(); x++) {
            this.craftingInv.setItem(x, this.gridInv.getStackInSlot(x));
        }
        var positionedInput = this.craftingInv.asPositionedCraftInput();
        var craftinginput = positionedInput.input();
        var output = this.assemblePattern(craftinginput);
        if (!output.isEmpty()) {
            List<GenericStack> left = new ArrayList<>();
            for (var stack : pattern.getRemainingItems(craftinginput)) {
                if (stack != null && !stack.isEmpty()) {
                    var gs = GenericStack.fromItemStack(stack);
                    if (gs != null) {
                        left.add(gs);
                    }
                }
            }
            return Pair.of(left, output);
        }
        return null;
    }

    private void fillGridFromArray(SearchResult result) {
        ((IntruderInventory) this.gridInv).setStacks(result.results);
    }

    private record SearchResult(ItemStack[] results, List<GenericStack> remains, ItemStack output, AEItemKey outputKey, int amount) {

        static SearchResult NULL = new SearchResult(null, null, null, null, 0);

        boolean isValid() {
            return this != NULL || this.results != null;
        }

    }

}
