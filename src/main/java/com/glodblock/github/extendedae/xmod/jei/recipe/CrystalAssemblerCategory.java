package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalAssemblerCategory implements IRecipeCategory<CrystalAssemblerRecipe>  {

    public static RecipeType<CrystalAssemblerRecipe> RECIPE_TYPE = RecipeType.create(ExtendedAE.MODID, "assembler", CrystalAssemblerRecipe.class);
    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final IDrawable icon;

    public CrystalAssemblerCategory(IGuiHelper helpers) {
        ResourceLocation texture = AppEng.makeId("textures/guis/crystal_assembler.png");
        this.background = helpers.createDrawable(texture, 23, 19, 135, 58);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(texture, 176, 0, 6, 18).addPadding(20, 0, 129, 0).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.icon = helpers.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EAESingletons.CRYSTAL_ASSEMBLER));
    }

    @Override
    public @NotNull RecipeType<CrystalAssemblerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return EAESingletons.CRYSTAL_ASSEMBLER.getName();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getHeight() {
        return 58;
    }

    @Override
    public int getWidth() {
        return 135;
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
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics);
    }

}
