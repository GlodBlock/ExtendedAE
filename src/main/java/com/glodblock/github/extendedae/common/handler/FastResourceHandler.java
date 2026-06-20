package com.glodblock.github.extendedae.common.handler;

import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.inventory.FastInternalInventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FastResourceHandler extends SnapshotJournal<FastResourceHandler.Snapshot> implements ResourceHandler<@NotNull ItemResource>, IndexModifier<@NotNull ItemResource> {

    private final FastInternalInventory inventory;
    @Nullable
    private FastResourceHandler.Snapshot lastReleasedSnapshot;

    public FastResourceHandler(FastInternalInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void set(int index, ItemResource resource, int amount) {
        this.inventory.setItemDirect(index, resource.toStack(amount));
    }

    @Override
    public int insert(ItemResource resource, int maxAmount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, maxAmount);
        var stack = resource.toStack(maxAmount);
        updateSnapshots(transaction);
        var overflow = this.inventory.addItemsSilent(stack);
        return maxAmount - overflow.getCount();
    }

    @Override
    public int extract(ItemResource resource, int maxAmount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, maxAmount);
        if (resource.getItem() == AEItems.WRAPPED_GENERIC_STACK.asItem()) {
            return 0;
        }
        updateSnapshots(transaction);
        ItemStack extracted = this.inventory.extractItemsSilent(maxAmount, resource.toStack());
        return extracted.getCount();
    }

    @Override
    public int insert(int index, ItemResource resource, int maxAmount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, maxAmount);
        updateSnapshots(transaction);
        var overflow = this.inventory.insertItemSilent(index, resource.toStack(maxAmount)).getCount();
        return maxAmount - overflow;
    }

    @Override
    public int extract(int index, ItemResource resource, int maxAmount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, maxAmount);
        if (resource.getItem() == AEItems.WRAPPED_GENERIC_STACK.asItem()) {
            return 0;
        }
        updateSnapshots(transaction);
        return this.inventory.extractItemSilent(index, maxAmount).getCount();
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return this.inventory.isItemValid(index, resource.toStack());
    }

    @Override
    public ItemResource getResource(int index) {
        return ItemResource.of(inventory.getStackInSlot(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        return this.inventory.getStackInSlot(index).getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return this.inventory.getSlotLimit(index);
    }

    @Override
    protected Snapshot createSnapshot() {
        Snapshot snapshot;
        if (this.lastReleasedSnapshot != null && this.lastReleasedSnapshot.items.length == this.inventory.size()) {
            snapshot = this.lastReleasedSnapshot;
            this.lastReleasedSnapshot = null;
        } else {
            snapshot = new Snapshot();
        }

        for (int i = 0; i < this.inventory.size(); i++) {
            var stack = this.inventory.getStackInSlot(i);
            snapshot.items[i] = stack;
            snapshot.counts[i] = stack.getCount();
        }
        return snapshot;
    }

    @Override
    protected void revertToSnapshot(Snapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        var items = snapshot.items;
        var counts = snapshot.counts;
        for (int i = 0; i < items.length; i++) {
            var stack = items[i];
            if (stack.getCount() != counts[i]) {
                stack.setCount(counts[i]);
            }
            this.inventory.setItemSilent(i, stack);
        }
    }

    @Override
    protected void releaseSnapshot(Snapshot snapshot) {
        this.lastReleasedSnapshot = snapshot;
    }

    public class Snapshot {
        ItemStack[] items;
        int[] counts;

        public Snapshot() {
            this.items = new ItemStack[inventory.size()];
            this.counts = new int[inventory.size()];
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onRootCommit(Snapshot original) {
        if (original == null) {
            for (int i = 0; i < this.inventory.size(); i++) {
                this.inventory.sendChangeNotification(i);
                this.inventory.onContentsChanged(i);
            }
        } else {
            for (int i = 0; i < original.items.length; i++) {
                var current = this.inventory.getStackInSlot(i);
                if (!ItemStack.isSameItemSameComponents(current, original.items[i]) || current.getCount() != original.counts[i]) {
                    this.inventory.sendChangeNotification(i);
                    this.inventory.onContentsChanged(i);
                }
            }
        }
    }

}
