package com.glodblock.github.extendedae.xmod.aae;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerAdvProcessingPattern extends ContainerProcessingPattern {

    public static final Identifier ID = ExtendedAE.id("adv_proc_pattern");
    public static final MenuType<@NotNull ContainerAdvProcessingPattern> TYPE = PatternGuiHandler.register(ID, ContainerAdvProcessingPattern::new);

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
