package com.glodblock.github.extendedae.xmod.jei.recipe;

import appeng.core.AppEng;
import appeng.util.ReadableNumberConverter;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.PulverizerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.rendering.IIngredientRenderContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.library.render.ItemStackRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class PulverizerCategory extends EAERecipeCategory<PulverizerRecipe>  {

    public static IRecipeType<@NotNull RecipeHolder<@NotNull PulverizerRecipe>> RECIPE_TYPE = IRecipeType.create(PulverizerRecipe.TYPE);
    private static final DecimalFormat F = new DecimalFormat("#.#%", new DecimalFormatSymbols());
    private final IDrawableAnimated progress;
    private final IDrawableStatic energyIcon;

    public PulverizerCategory(IGuiHelper helpers) {
        super(helpers, RECIPE_TYPE, EAESingletons.PULVERIZER, 114, 60);
        var texture = AppEng.makeId("textures/guis/pulverizer.png");
        this.background = helpers.createDrawable(texture, 31, 20, 114, 60);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(texture, 176, 0, 6, 18).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.energyIcon = helpers.createDrawable(AppEng.makeId("textures/xei/xei_icons.png"), 0, 0, 16, 16);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull PulverizerRecipe recipe, @NotNull IFocusGroup focuses) {
        JEIStackUtil.addItem(recipe.getInput(), builder.addSlot(RecipeIngredientRole.INPUT, 3, 14).setSlotName("input"));
        int x = 68;
        int y = 5;
        var outputs = recipe.getOutputs();
        for (var output : outputs) {
            if (output.chance() > 0) {
                var stack = output.stack().create();
                if (!stack.isEmpty()) {
                    var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y).setSlotName("output");
                    if (output.chance() < PulverizerRecipe.ChanceOutput.FULL_CHANCE) {
                        slot.setCustomRenderer(VanillaTypes.ITEM_STACK, new ChanceRender(F.format(PulverizerRecipe.ChanceOutput.readableChance(output.chance()))));
                    }
                    slot.add(stack);
                }
                x += 18;
                if (x >= 18 * 2 + 68) {
                    y += 18;
                    x = 68;
                }
            }
        }
    }

    @Override
    public void draw(@NotNull PulverizerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.progress.draw(guiGraphics, 108, 13);
        this.energyIcon.draw(guiGraphics, 2, 44);
        guiGraphics.text(Minecraft.getInstance().font, Component.translatable("emi.extendedae.text.energy", ReadableNumberConverter.format(recipe.energy, 5)), 20, 48, 0xFF7E7E7E, false);
    }

    private static class ChanceRender extends ItemStackRenderer {

        private final String chance;

        private ChanceRender(String chance) {
            this.chance = chance;
        }

        @Override
        public void render(@NotNull GuiGraphicsExtractor guiGraphics, @Nullable ItemStack ingredient, int posX, int posY, @NotNull IIngredientRenderContext context) {
            super.render(guiGraphics, ingredient, posX, posY, context);
            var pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate(posX, posY);
            pose.scale(0.6f, 0.6f);
            guiGraphics.text(Minecraft.getInstance().font, this.chance, 0, 0, 0xFFFFFF00, true);
            pose.popMatrix();
        }

        @Override
        public @NotNull List<Component> getTooltip(@NotNull ItemStack ingredient, Item.@NotNull TooltipContext tooltipContext, @Nullable Player player, @NotNull TooltipFlag tooltipFlag) {
            var tooltip = super.getTooltip(ingredient, tooltipContext, player, tooltipFlag);
            tooltip.add(Component.translatable("emi.extendedae.text.output_chance", this.chance).withStyle(ChatFormatting.YELLOW));
            return tooltip;
        }

    }

}
