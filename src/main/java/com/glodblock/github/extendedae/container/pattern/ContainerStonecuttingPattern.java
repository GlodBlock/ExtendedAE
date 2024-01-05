package com.glodblock.github.extendedae.container.pattern;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEStonecuttingPattern;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ContainerStonecuttingPattern extends ContainerPattern {

    public static final ResourceLocation ID = ExtendedAE.id("stonecut_pattern");
    public static final MenuType<ContainerStonecuttingPattern> TYPE = PatternGuiHandler.register(ID.toString(), ContainerStonecuttingPattern::new);

    public ContainerStonecuttingPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(TYPE, world, id, stack);
        this.addSlot(new DisplayOnlySlot(this, this.inputs, 0, 51, 25));
        this.addSlot(new DisplayOnlySlot(this, this.outputs, 0, 106, 25));
    }

    @Override
    protected void analyse() {
        if (this.details instanceof AEStonecuttingPattern pattern) {
            var rawInput = clean(pattern.getInputs()[0].getPossibleInputs());
            var inStacks = new GenericStack[rawInput.length];
            for (int i = 0; i < inStacks.length; i ++) {
                inStacks[i] = new GenericStack(rawInput[i].what(), rawInput[i].amount() * pattern.getInputs()[0].getMultiplier());
            }
            this.inputs.add(inStacks);
            var rawOutputs = pattern.getPrimaryOutput();
            this.outputs.add(new GenericStack[] {new GenericStack(rawOutputs.what(), rawOutputs.amount())});
        } else {
            this.invalidate();
        }
    }

}
