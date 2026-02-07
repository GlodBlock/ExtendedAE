package com.glodblock.github.extendedae.xmod.jei.transfer;

import appeng.api.stacks.AEItemKey;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import appeng.util.CraftingRecipeUtil;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Comparator;
import java.util.Map;

public class ExCraftingHelper {

    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator.comparing(GridInventoryEntry::getStoredAmount);

    public static void performTransfer(ContainerExCraftingTerminal menu, Recipe<?> recipe, int recipeSize, boolean craftMissing) {
        // We send the items in the recipe in any case to serve as a fallback in case the recipe is transient
        var templateItems = findGoodTemplateItems(recipe, recipeSize, menu);
        var recipeId = recipe.getId();
        // Don't transmit a recipe id to the server in case the recipe is not actually resolvable
        // this is the case for recipes synthetically generated for JEI
        if (menu.getPlayer().level().getRecipeManager().byKey(recipe.getId()).isEmpty()) {
            AELog.debug("Cannot send recipe id %s to server because it's transient", recipeId);
            recipeId = null;
        }
        NetworkHandler.instance().sendToServer(new FillCraftingGridFromRecipePacket(recipeId, templateItems, craftMissing));
    }

    private static NonNullList<ItemStack> findGoodTemplateItems(Recipe<?> recipe, int recipeSize, MEStorageMenu menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);
        var templateItems = NonNullList.withSize(recipeSize, ItemStack.EMPTY);
        var ingredients = CraftingRecipeUtil.ensure3by3CraftingMatrix(recipe);
        for (int i = 0; i < Math.min(ingredients.size(), recipeSize); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                // Try to find the best item. In case the ingredient is a tag, it might contain versions the
                // player doesn't actually have
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }

}
