package com.glodblock.github.extendedae.container.pattern;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContainerProcessingPattern extends ContainerPattern {

    public static final Identifier ID = ExtendedAE.id("proc_pattern");
    public static final MenuType<ContainerProcessingPattern> TYPE = PatternGuiHandler.register(ID, ContainerProcessingPattern::new);

    public ContainerProcessingPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(menuType, world, id, stack);
        for (int row = 0; row < 9; row ++) {
            for (int col = 0; col < 9; col ++) {
                this.addSlot(new DisplayOnlySlot(this, this.inputs, row * 9 + col, 8 + col * 18, 9 + row * 18));
            }
        }
        for (int row = 0; row < 3; row ++) {
            for (int col = 0; col < 9; col ++) {
                this.addSlot(new DisplayOnlySlot(this, this.outputs, row * 9 + col, 8 + col * 18, 189 + row * 18));
            }
        }
    }

    @Override
    protected void analyse() {
        if (this.details instanceof AEProcessingPattern pattern) {
            this.addContents(pattern.getSparseInputs(), this.inputs);
            this.addContents(pattern.getSparseOutputs(), this.outputs);
        } else {
            this.invalidate();
        }
    }

    protected void addContents(List<GenericStack> stacks, List<GenericStack[]> slots) {
        for (var stack : stacks) {
            if (stack == null) {
                slots.add(new GenericStack[0]);
                continue;
            }
            var outStacks = new GenericStack[] {new GenericStack(stack.what(), stack.amount())};
            slots.add(outStacks);
        }
    }

}
