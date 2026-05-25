package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class CrystalAssemblerCategory extends EAERecipeCategory<CrystalAssemblerRecipe>  {

    public static RecipeType<RecipeHolder<CrystalAssemblerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(CrystalAssemblerRecipe.TYPE);
    private final IDrawableAnimated progress;

    public CrystalAssemblerCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, EAESingletons.CRYSTAL_ASSEMBLER, 135, 58);
        ResourceLocation texture = AppEng.makeId("textures/guis/crystal_assembler.png");
        this.background = helpers.createDrawable(texture, 23, 19, 135, 58);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(texture, 176, 0, 6, 18).addPadding(20, 0, 129, 0).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CrystalAssemblerRecipe recipe, @NotNull IFocusGroup focuses) {
        int x = 3;
        int y = 3;
        for (var in : recipe.getInputs()) {
            if (!in.isEmpty()) {
                JEIStackUtil.addItem(in, builder.addSlot(RecipeIngredientRole.INPUT, x, y));
                x += 18;
                if (x >= 18 * 3) {
                    y += 18;
                    x = 3;
                }
            }
        }
        if (recipe.getFluid() != null) {
            JEIStackUtil.addFluid(recipe.getFluid(), builder.addSlot(RecipeIngredientRole.INPUT, 58, 39).setSlotName("fluid_input"));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 21).setSlotName("output").addItemStack(recipe.output);
    }

    @Override
    public void draw(@NotNull CrystalAssemblerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.progress.draw(guiGraphics);
    }

}
