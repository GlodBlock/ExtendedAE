package com.glodblock.github.extendedae.container.pattern;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import org.jetbrains.annotations.Nullable;

public class ContainerAdvProcessingPattern extends ContainerProcessingPattern {

    public static final ResourceLocation ID = ExtendedAE.id("adv_proc_pattern");
    public static final MenuType<ContainerAdvProcessingPattern> TYPE = PatternGuiHandler.register(ID, ContainerAdvProcessingPattern::new);

    public ContainerAdvProcessingPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(menuType, id, world, stack);
    }

    @Override
    protected void analyse() {
        if (this.details instanceof AdvProcessingPattern pattern) {
            this.addContents(pattern.getSparseInputs(), this.inputs);
            this.addContents(pattern.getSparseOutputs(), this.outputs);
        } else {
            this.invalidate();
        }
    }

}
