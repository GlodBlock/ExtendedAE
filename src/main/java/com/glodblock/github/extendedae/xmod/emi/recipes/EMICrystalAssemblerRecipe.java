package com.glodblock.github.extendedae.xmod.emi.recipes;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class EMICrystalAssemblerRecipe extends BasicEmiRecipe {

    public static final EmiRecipeCategory CATEGORY = new EAERecipeCategory("assembler", EmiStack.of(EAEItemAndBlock.CRYSTAL_ASSEMBLER), Component.translatable("emi.extendedae.category.assembler"));
    private final CrystalAssemblerRecipe recipe;

    public EMICrystalAssemblerRecipe(RecipeHolder<CrystalAssemblerRecipe> holder) {
        super(CATEGORY, holder.id(), 133, 54);
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
        widgets.addTexture(background, 0, 0, 133, 54, 25, 21);
        widgets.addAnimatedTexture(background, 127, 18, 6, 18, 179, 39, 2000, false, true, false);
        int x = 0;
        int y = 0;
        for (var in : this.recipe.getInputs()) {
            if (!in.isEmpty()) {
                widgets.addSlot(EMIStackUtil.of(in), x, y).drawBack(false);
                x += 18;
                if (x >= 18 * 3) {
                    y += 18;
                    x = 0;
                }
            }
        }
        if (this.recipe.getFluid() != null) {
            widgets.addSlot(EMIStackUtil.of(this.recipe.getFluid()), 54, 36).drawBack(false);
        }
        widgets.addSlot(EmiStack.of(recipe.output), 104, 18).drawBack(false);
    }
}
