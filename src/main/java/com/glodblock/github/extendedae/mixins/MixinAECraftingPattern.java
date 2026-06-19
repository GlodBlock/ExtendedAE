package com.glodblock.github.extendedae.mixins;

import appeng.crafting.pattern.AECraftingPattern;
import com.glodblock.github.extendedae.util.helper.CraftingRecipePattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(AECraftingPattern.class)
public class MixinAECraftingPattern implements CraftingRecipePattern {

    @Final
    @Shadow(remap = false)
    private ItemStack output;
    @Final
    @Shadow(remap = false)
    private RecipeHolder<?> recipeHolder;

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public RecipeHolder<?> getRecipeHolder() {
        return this.recipeHolder;
    }

}
