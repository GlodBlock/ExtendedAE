package com.github.glodblock.epp.container.pattern;

import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AESmithingTablePattern;
import com.github.glodblock.epp.EPP;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ContainerSmithingTablePattern extends ContainerPattern {

    public static final ResourceLocation ID = EPP.id("smithing_table_pattern");
    public static final MenuType<ContainerSmithingTablePattern> TYPE = PatternGuiHandler.register(ID.toString(), ContainerSmithingTablePattern::new);

    public ContainerSmithingTablePattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(TYPE, world, id, stack);
        for (int i = 0; i < 3; i ++) {
            this.addSlot(new DisplayOnlySlot(this, this.inputs, i, 34 + i * 18, 25));
        }
        this.addSlot(new DisplayOnlySlot(this, this.outputs, 0, 124, 25));
    }

    @Override
    protected void analyse() {
        if (this.details instanceof AESmithingTablePattern pattern) {
            var rawInputs = pattern.getInputs();
            for (var in : rawInputs) {
                var possIn = clean(in.getPossibleInputs());
                var inStacks = new GenericStack[possIn.length];
                for (int i = 0; i < possIn.length; i ++) {
                    inStacks[i] = new GenericStack(possIn[i].what(), possIn[i].amount());
                }
                this.inputs.add(inStacks);
            }
            var rawOutputs = pattern.getPrimaryOutput();
            this.outputs.add(new GenericStack[] {new GenericStack(rawOutputs.what(), rawOutputs.amount())});
        } else {
            this.invalidate();
        }
    }

}
