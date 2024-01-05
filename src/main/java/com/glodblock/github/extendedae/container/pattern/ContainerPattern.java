package com.glodblock.github.extendedae.container.pattern;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.items.misc.WrappedGenericStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class ContainerPattern extends AbstractContainerMenu {

    protected final ItemStack stack;
    protected final IPatternDetails details;
    protected final List<GenericStack[]> inputs = new ArrayList<>();
    protected final List<GenericStack[]> outputs = new ArrayList<>();
    private boolean valid = true;
    private int cycle = 0;

    public ContainerPattern(@Nullable MenuType<?> menuType, Level world, int id, ItemStack stack) {
        super(menuType, id);
        this.stack = stack;
        var item = this.stack.getItem();
        if (item instanceof EncodedPatternItem pattern) {
            this.details = pattern.decode(stack, world, true);
            this.analyse();
        } else {
            throw new IllegalArgumentException(String.format("%s isn't an encoded pattern!", item));
        }
    }

    protected abstract void analyse();

    public void invalidate() {
        this.valid = false;
    }

    public void setCycleItem(int index) {
        this.cycle = index;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int idx) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.details != null && this.valid;
    }

    protected GenericStack[] clean(GenericStack[] in) {
        Set<GenericStack> set = new ObjectOpenHashSet<>();
        set.addAll(Arrays.asList(in));
        var c = new GenericStack[set.size()];
        int i = 0;
        for (var tmp : set) {
            c[i] = tmp;
            i ++;
        }
        return c;
    }

    public static class DisplayOnlySlot extends Slot {

        private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
        private final ItemStack[] displays;
        private final int index;
        private final ContainerPattern container;
        private long size = 0;

        public DisplayOnlySlot(ContainerPattern container, List<GenericStack[]> stacks, int index, int x, int y) {
            super(EMPTY_INVENTORY, 0, x, y);
            if (index >= stacks.size()) {
                this.displays = new ItemStack[0];
            } else {
                var genStack = stacks.get(index);
                this.displays = new ItemStack[genStack.length];
                if (genStack.length > 0) {
                    for (int i = 0; i < genStack.length; i ++) {
                        this.displays[i] = getAsItem(genStack[i]);
                    }
                }
            }
            this.index = index;
            this.container = container;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return false;
        }

        @Override
        public @NotNull ItemStack getItem() {
            return getItem(this.container.cycle);
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            // NO-OP
        }

        public @NotNull ItemStack getItem(int rowIndex) {
            if (this.displays.length > 0) {
                rowIndex = rowIndex % this.displays.length;
                return this.displays[rowIndex];
            }
            return ItemStack.EMPTY;
        }

        // For item stack size rendering
        public long getActualAmount() {
            return this.size;
        }

        public boolean shouldUseMEText() {
            return !(this.getItem().getItem() instanceof WrappedGenericStack);
        }

        @Override
        public int getSlotIndex() {
            return this.index;
        }

        @NotNull
        private ItemStack getAsItem(GenericStack stack) {
            if (stack != null && stack.what() instanceof AEItemKey itemKey) {
                this.size = stack.amount();
                return itemKey.toStack();
            }
            return GenericStack.wrapInItemStack(stack);
        }

    }

}
