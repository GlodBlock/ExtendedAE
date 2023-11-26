package com.github.glodblock.extendedae.container.pattern;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import com.github.glodblock.extendedae.EAE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ContainerProcessingPattern extends ContainerPattern {

    public static final ResourceLocation ID = EAE.id("proc_pattern");
    public static final MenuType<ContainerProcessingPattern> TYPE = PatternGuiHandler.register(ID.toString(), ContainerProcessingPattern::new);

    public ContainerProcessingPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(TYPE, world, id, stack);
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
            var rawInputs = pattern.getSparseInputs();
            for (var in : rawInputs) {
                if (in == null) {
                    this.inputs.add(new GenericStack[0]);
                    continue;
                }
                var inStacks = new GenericStack[] {new GenericStack(in.what(), in.amount())};
                this.inputs.add(inStacks);
            }
            var rawOutputs = pattern.getSparseOutputs();
            for (var out : rawOutputs) {
                if (out == null) {
                    this.outputs.add(new GenericStack[0]);
                    continue;
                }
                var outStacks = new GenericStack[] {new GenericStack(out.what(), out.amount())};
                this.outputs.add(outStacks);
            }
        } else {
            this.invalidate();
        }
    }

}
