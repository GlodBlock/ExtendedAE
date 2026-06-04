package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import appeng.util.ReadableNumberConverter;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

public class CircuitCutterCategory extends EAERecipeCategory<CircuitCutterRecipe> {

    public static IRecipeType<@NotNull RecipeHolder<@NotNull CircuitCutterRecipe>> RECIPE_TYPE = IRecipeType.create(CircuitCutterRecipe.TYPE);
    private final IDrawableAnimated progress;
    private final IDrawableStatic energyIcon;

    public CircuitCutterCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, EAESingletons.CIRCUIT_CUTTER, 94, 43);
        var texture = AppEng.makeId("textures/guis/circuit_cutter.png");
        this.background = helpers.createDrawable(texture, 43, 32, 94, 43);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(texture, 176, 0, 6, 18).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.energyIcon = helpers.createDrawable(AppEng.makeId("textures/xei/xei_icons.png"), 0, 0, 16, 16);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CircuitCutterRecipe recipe, @NotNull IFocusGroup focuses) {
        JEIStackUtil.addItem(recipe.getInput(), builder.addSlot(RecipeIngredientRole.INPUT, 3, 5).setSlotName("input"));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 5).setSlotName("output").add(recipe.output);
    }

    @Override
    public void draw(@NotNull CircuitCutterRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.progress.draw(guiGraphics, 88, 4);
        this.energyIcon.draw(guiGraphics, 2, 26);
        guiGraphics.text(Minecraft.getInstance().font, Component.translatable("emi.extendedae.text.energy", ReadableNumberConverter.format(recipe.energy, 5)), 20, 30, 0xFF7E7E7E, false);
    }

}
