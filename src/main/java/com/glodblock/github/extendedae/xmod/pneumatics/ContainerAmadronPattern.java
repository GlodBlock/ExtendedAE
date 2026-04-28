package com.glodblock.github.extendedae.xmod.pneumatics;

import appeng.api.stacks.GenericStack;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ContainerAmadronPattern extends ContainerPattern {

    public static final ResourceLocation ID = ExtendedAE.id("amadron");
    public static final MenuType<ContainerAmadronPattern> TYPE = PatternGuiHandler.register(ID, ContainerAmadronPattern::new);

    public ContainerAmadronPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(menuType, world, id, stack);
        this.addSlot(new DisplayOnlySlot(this, this.inputs, 0, 51, 25));
        this.addSlot(new DisplayOnlySlot(this, this.outputs, 0, 106, 25));
    }

    @Override
    protected void analyse() {
        try {
            var rawInput = this.details.getInputs()[0];
            this.inputs.add(new GenericStack[] {new GenericStack(rawInput.getPossibleInputs()[0].what(), rawInput.getMultiplier())});
            var rawOutputs = this.details.getPrimaryOutput();
            this.outputs.add(new GenericStack[] {new GenericStack(rawOutputs.what(), rawOutputs.amount())});
        } catch (Throwable t) {
            this.invalidate();
        }
    }

}
