package com.glodblock.github.extendedae.xmod.emi.recipes;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EMICrystalAssemblerRecipe extends BasicEmiRecipe {

    public static final EmiRecipeCategory CATEGORY = new EAERecipeCategory("assembler", EmiStack.of(EAESingletons.CRYSTAL_ASSEMBLER), Component.translatable("emi.extendedae.category.assembler"));
    private final CrystalAssemblerRecipe recipe;

    public EMICrystalAssemblerRecipe(RecipeHolder<CrystalAssemblerRecipe> holder) {
        super(CATEGORY, holder.id(), 135, 58);
        this.recipe = holder.value();
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                this.inputs.add(EMIStackUtil.of(in));
            }
        }
        if (this.recipe.getFluid() != null) {
            this.inputs.add(EMIStackUtil.of(this.recipe.getFluid()));
        }
        this.outputs.add(EmiStack.of(this.recipe.output));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = AppEng.makeId("textures/guis/crystal_assembler.png");
        widgets.addTexture(background, 0, 0, 135, 58, 23, 19);
        widgets.addAnimatedTexture(background, 129, 20, 6, 18, 176, 0, 2000, false, true, false);
        int x = 2;
        int y = 2;
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                widgets.addSlot(EMIStackUtil.of(in), x, y).drawBack(false);
                x += 18;
                if (x >= 18 * 3) {
                    y += 18;
                    x = 2;
                }
            }
        }
        if (this.recipe.getFluid() != null) {
            widgets.addSlot(EMIStackUtil.of(this.recipe.getFluid()), 57, 38).drawBack(false);
        }
        widgets.addSlot(EmiStack.of(recipe.output), 106, 20).recipeContext(this).drawBack(false);
    }
}
