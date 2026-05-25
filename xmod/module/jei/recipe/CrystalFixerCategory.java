package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CrystalFixerCategory extends EAERecipeCategory<CrystalFixerRecipe> {

    public static RecipeType<RecipeHolder<CrystalFixerRecipe>> RECIPE_TYPE = RecipeType.createFromVanilla(CrystalFixerRecipe.TYPE);
    private static final DecimalFormat F = new DecimalFormat("#.#%", new DecimalFormatSymbols());
    private final IDrawable display;

    public CrystalFixerCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, EAESingletons.CRYSTAL_FIXER, 114, 63);
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
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CrystalFixerRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19).setSlotName("input").addItemLike(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 19).setSlotName("output").addItemLike(recipe.getOutput());
        JEIStackUtil.addItem(recipe.getFuel(), builder.addSlot(RecipeIngredientRole.INPUT, 49, 12).setSlotName("fuel"));
    }

    @Override
    public void draw(@NotNull CrystalFixerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.display.draw(guiGraphics);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("emi.extendedae.text.success_chance", F.format(recipe.getChance())), 1, 2, 0x7E7E7E, false);
    }

}
