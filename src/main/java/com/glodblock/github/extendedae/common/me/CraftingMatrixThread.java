package com.glodblock.github.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CraftingMatrixThread extends CraftingThread {

    private static final int COOL_TIME = 5 * 20;
    private int blockCoolDown = 0;
    private final Supplier<IActionSource> sourceGetter;

    public CraftingMatrixThread(@NotNull AEBaseBlockEntity host, @NotNull Supplier<IActionSource> sourceGetter) {
        super(host);
        this.sourceGetter = sourceGetter;
    }

    @Override
    public TickRateModulation tick(int cards, int ticksSinceLastCall) {
        if (this.blockCoolDown > 0) {
            this.blockCoolDown -= ticksSinceLastCall;
            return TickRateModulation.SLOWER;
        } else {
            return super.tick(cards, ticksSinceLastCall);
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

}
