package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CrystalFixerCategory implements IRecipeCategory<CrystalFixerRecipe> {

    public static RecipeType<CrystalFixerRecipe> RECIPE_TYPE = RecipeType.create(ExtendedAE.MODID, "crystal_fixer", CrystalFixerRecipe.class);
    private static final DecimalFormat F = new DecimalFormat("#.#%", new DecimalFormatSymbols());
    private final IDrawable background;
    private final IDrawable display;
    private final IDrawable icon;

    public CrystalFixerCategory(IGuiHelper helpers) {
        ResourceLocation texture = AppEng.makeId("textures/xei/crystal_fixer.png");
        this.background = helpers.createDrawable(texture, 0, 0, 114, 63);
        this.display = new IDrawable() {
            @Override
            public int getWidth() {
                return 30;
            }

            @Override
            public int getHeight() {
                return 30;
            }

            @Override
            public void draw(@NotNull GuiGraphics guiGraphics, int xOffset, int yOffset) {
                var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
                renderContext.renderItem(new ItemStack(EAESingletons.CRYSTAL_FIXER), xOffset + 42, yOffset + 29, 30, 30);
            }
        };
        this.icon = helpers.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EAESingletons.CRYSTAL_FIXER));
    }

    @Override
    public @NotNull RecipeType<CrystalFixerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return EAESingletons.CRYSTAL_FIXER.getName();
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getHeight() {
        return 63;
    }

    @Override
    public int getWidth() {
        return 114;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CrystalFixerRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).setSlotName("input").addItemLike(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 19).setSlotName("output").addItemLike(recipe.getOutput());
        JEIStackUtil.addItem(recipe.getFuel(), builder.addSlot(RecipeIngredientRole.INPUT, 49, 12).setSlotName("fuel"));
    }

    @Override
    public void draw(@NotNull CrystalFixerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.display.draw(guiGraphics);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("emi.extendedae.text.success_chance", F.format(recipe.getChance())), 1, 2, 0x7E7E7E, false);
    }

}
