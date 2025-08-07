package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
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

public class CircuitCutterCategory implements IRecipeCategory<CircuitCutterRecipe> {

    public static RecipeType<CircuitCutterRecipe> RECIPE_TYPE = RecipeType.create(ExtendedAE.MODID, "circuit_cutter", CircuitCutterRecipe.class);
    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final IDrawable icon;

    public CircuitCutterCategory(IGuiHelper helpers) {
        ResourceLocation texture = AppEng.makeId("textures/guis/circuit_cutter.png");
        this.background = helpers.createDrawable(texture, 43, 32, 94, 26);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(texture, 176, 0, 6, 18).addPadding(4, 0, 88, 0).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.icon = helpers.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EAESingletons.CIRCUIT_CUTTER));
    }

    @Override
    public @NotNull RecipeType<CircuitCutterRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return EAESingletons.CIRCUIT_CUTTER.getName();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getHeight() {
        return 26;
    }

    @Override
    public int getWidth() {
        return 94;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CircuitCutterRecipe recipe, @NotNull IFocusGroup focuses) {
        JEIStackUtil.addItem(recipe.getInput(), builder.addSlot(RecipeIngredientRole.INPUT, 3, 5).setSlotName("input"));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 5).setSlotName("output").addItemStack(recipe.output);
    }

    @Override
    public void draw(@NotNull CircuitCutterRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics);
    }

}
