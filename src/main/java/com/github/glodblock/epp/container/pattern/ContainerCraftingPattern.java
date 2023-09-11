package com.github.glodblock.epp.container.pattern;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AECraftingPattern;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.util.Ae2Reflect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class ContainerCraftingPattern extends ContainerPattern {

    public static final ResourceLocation ID = EPP.id("craft_pattern");
    public static final MenuType<ContainerCraftingPattern> TYPE = PatternGuiHandler.register(ID.toString(), ContainerCraftingPattern::new);

    public ContainerCraftingPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(TYPE, world, id, stack);
        for (int row = 0; row < 3; row ++) {
            for (int col = 0; col < 3; col ++) {
                this.addSlot(new DisplayOnlySlot(this, this.inputs, row * 3 + col, 29 + col * 18, 35 + row * 18));
            }
        }
        this.addSlot(new DisplayOnlySlot(this, this.outputs, 0, 121, 53));
    }

    @Override
    protected void analyse() {
        if (this.details instanceof AECraftingPattern pattern) {
            var rawInputs = pattern.getInputs();
            var tmpInputs = new GenericStack[9][];
            for (int i = 0; i < 9; i ++) {
                int cid = Ae2Reflect.getCompressIndex(pattern, i);
                if (cid == -1) {
                    tmpInputs[i] = new GenericStack[0];
                    continue;
                }
                var in = clean(rawInputs[cid].getPossibleInputs());
                var inStacks = new GenericStack[in.length];
                for (int j = 0; j < inStacks.length; j ++) {
                    inStacks[j] = new GenericStack(in[j].what(), in[j].amount());
                }
                tmpInputs[i] = inStacks;
            }
            Collections.addAll(this.inputs, tmpInputs);
            var rawOutputs = pattern.getPrimaryOutput();
            this.outputs.add(new GenericStack[] {new GenericStack(rawOutputs.what(), rawOutputs.amount())});
        } else {
            this.invalidate();
        }
    }

    public boolean canSubstitute() {
        return this.details instanceof AECraftingPattern pattern && pattern.canSubstitute();
    }

    public boolean canSubstituteFluids() {
        return this.details instanceof AECraftingPattern pattern && pattern.canSubstituteFluids();
    }

}
