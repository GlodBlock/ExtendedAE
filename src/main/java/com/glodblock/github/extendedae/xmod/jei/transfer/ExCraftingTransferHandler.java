package com.glodblock.github.extendedae.xmod.jei.transfer;

import appeng.core.localization.ItemModText;
import appeng.integration.modules.jei.JEIPlugin;
import appeng.integration.modules.jei.transfer.AbstractTransferHandler;
import appeng.integration.modules.jeirei.CompatLayerHelper;
import appeng.integration.modules.jeirei.TransferHelper;
import appeng.integration.modules.rei.transfer.UseCraftingRecipeTransfer;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.util.CraftingRecipeUtil;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ExCraftingTransferHandler<T extends ContainerExCraftingTerminal> extends AbstractTransferHandler implements IRecipeTransferHandler<T, Object> {

    private final MenuType<T> menuType;
    private final Class<T> menuClass;
    private final IRecipeTransferHandlerHelper helper;

    public ExCraftingTransferHandler(MenuType<T> menuType, Class<T> menuClass, IRecipeTransferHandlerHelper helper) {
        this.menuType = menuType;
        this.menuClass = menuClass;
        this.helper = helper;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(@NotNull T container, @NotNull Object obj, @NotNull IRecipeSlotsView recipeSlots, @NotNull Player player, boolean maxTransfer, boolean doTransfer) {
        if (obj instanceof Recipe<?> recipe) {
            var type = recipe.getType();
            boolean craftMissing = AbstractContainerScreen.hasControlDown();
            if (type == RecipeType.CRAFTING) {
                return this.handlerCraft(recipe, container, recipeSlots, doTransfer, craftMissing);
            }
            if (type == RecipeType.STONECUTTING) {
                return this.handlerRecipe(recipe, CraftingMode.STONECUTTER, getGuiSlotToIngredientMapPlain(recipe), container, recipeSlots, 1, doTransfer, craftMissing);
            }
            if (type == RecipeType.SMITHING) {
                return this.handlerRecipe(recipe, CraftingMode.SMITHING, getGuiSlotToIngredientMapPlain(recipe), container, recipeSlots, 3, doTransfer, craftMissing);
            }
        }
        return this.helper.createInternalError();
    }

    private IRecipeTransferError handlerCraft(Recipe<?> recipe, T menu, IRecipeSlotsView display, boolean doTransfer, boolean craftMissing) {
        if (recipe.getIngredients().isEmpty()) {
            return this.helper.createUserErrorWithTooltip(ItemModText.INCOMPATIBLE_RECIPE.text());
        }
        if (!recipe.canCraftInDimensions(CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT)) {
            return this.helper.createUserErrorWithTooltip(ItemModText.RECIPE_TOO_LARGE.text());
        }
        return this.handlerRecipe(recipe, CraftingMode.CRAFTING, getGuiSlotToIngredientMap(recipe), menu, display, 9, doTransfer, craftMissing);
    }

    private IRecipeTransferError handlerRecipe(Recipe<?> recipe, CraftingMode mode, Map<Integer, Ingredient> slotToIngredientMap, T menu, IRecipeSlotsView display, int recipeSize, boolean doTransfer, boolean craftMissing) {
        var inputSlots = display.getSlotViews(RecipeIngredientRole.INPUT);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);
        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();
            return this.helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }
        // Find missing ingredients and highlight the slots which have these
        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? TransferHelper.ORANGE_PLUS_BUTTON_COLOR : TransferHelper.BLUE_PLUS_BUTTON_COLOR;
                return new ErrorRenderer(missingSlots, craftMissing, color);
            }
        } else {
            this.switchMode(mode);
            ExCraftingHelper.performTransfer(menu, recipe, recipeSize, craftMissing);
            if (mode == CraftingMode.STONECUTTER) {
                EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("stonecutter_select", recipe.getId().toString()));
            }
        }
        // No error
        return null;
    }

    private void switchMode(CraftingMode mode) {
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set_mode", mode.ordinal()));
    }

    @Override
    public @NotNull Class<? extends T> getContainerClass() {
        return this.menuClass;
    }

    @Override
    public @NotNull Optional<MenuType<T>> getMenuType() {
        return Optional.of(this.menuType);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull mezz.jei.api.recipe.RecipeType<Object> getRecipeType() {
        return null;
    }

    private static Map<Integer, Ingredient> getGuiSlotToIngredientMapPlain(Recipe<?> recipe) {
        var inputs = CraftingRecipeUtil.getIngredients(recipe);
        Map<Integer, Ingredient> ingredientMap = new HashMap<>(inputs.size());
        for (int i = 0; i < inputs.size(); i++) {
            ingredientMap.put(i, inputs.get(i));
        }
        return ingredientMap;
    }

    private static Map<Integer, Ingredient> getGuiSlotToIngredientMap(Recipe<?> recipe) {
        if (CompatLayerHelper.IS_LOADED) {
            return UseCraftingRecipeTransfer.getGuiSlotToIngredientMap(recipe);
        }
        var ingredients = recipe.getIngredients();
        // JEI will align non-shaped recipes smaller than 3x3 in the grid. It'll center them horizontally, and
        // some will be aligned to the bottom. (i.e. slab recipes).
        int width, height;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
            height = shapedRecipe.getHeight();
        } else {
            if (ingredients.size() > 4) {
                width = height = 3;
            } else if (ingredients.size() > 1) {
                width = height = 2;
            } else {
                width = height = 1;
            }
        }
        var result = new LinkedHashMap<Integer, Ingredient>(ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            var guiSlot = getCraftingIndex(i, width, height);
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                result.put(guiSlot, ingredient);
            }
        }
        return result;
    }

    private static int getCraftingIndex(int i, int width, int height) {
        int index;
        if (width == 1) {
            if (height == 3) {
                index = (i * 3) + 1;
            } else if (height == 2) {
                index = (i * 3) + 1;
            } else {
                index = 4;
            }
        } else if (height == 1) {
            index = i + 3;
        } else if (width == 2) {
            index = i;
            if (i > 1) {
                index++;
                if (i > 3) {
                    index++;
                }
            }
        } else if (height == 2) {
            index = i + 3;
        } else {
            index = i;
        }
        return index;
    }

    private record ErrorRenderer(CraftingTermMenu.MissingIngredientSlots indices, boolean craftMissing, int color) implements IRecipeTransferError {

        @Override
        public @NotNull Type getType() {
            return Type.COSMETIC;
        }

        @Override
        public int getButtonHighlightColor() {
            return this.color;
        }

        @Override
        public void showError(GuiGraphics guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX, int recipeY) {
            var poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(recipeX, recipeY, 0);

            // 1) draw slot highlights
            var slotViews = slots.getSlotViews(RecipeIngredientRole.INPUT);
            for (int i = 0; i < slotViews.size(); i++) {
                var slotView = slotViews.get(i);
                boolean missing = this.indices.missingSlots().contains(i);
                boolean craftable = this.indices.craftableSlots().contains(i);
                if (missing || craftable) {
                    slotView.drawHighlight(guiGraphics, missing ? TransferHelper.RED_SLOT_HIGHLIGHT_COLOR : TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR);
                }
            }

            poseStack.popPose();

            // 2) draw tooltip
            var tooltip = TransferHelper.createCraftingTooltip(indices, craftMissing);
            JEIPlugin.drawHoveringText(guiGraphics, tooltip, mouseX, mouseY);
        }
    }

}
