package com.glodblock.github.extendedae.xmod.emi;

import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.menu.implementations.UpgradeableMenu;

import com.glodblock.github.extendedae.util.FakeSlotTransferHelper;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.widget.Widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.ArrayList;
import java.util.List;
// Note: this class is also in a PR to base AE2 at https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/8915
// Should that PR be accepted, it will no longer be necessary in ExtendedAE.
public class FakeSlotTransferHandler<T extends UpgradeableMenu<? extends IUpgradeableObject>>
        implements EmiRecipeHandler<T> {

    /**
     * @param screen
     * @return An inventory with the stacks the player can use for crafting. Craftables can only
     *     ever be discovered if the inventory contains one of its ingredients. A changed inventory
     *     indicates that EMI needs to refresh craftables.
     */
    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
        return new EmiPlayerInventory(new ArrayList<>());
    }

    /**
     * @param recipe
     * @return Whether the handler is applicable for the provided recipe.
     */
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return true;
    }

    /**
     * @param recipe
     * @return Whether the recipe should always display the ability to be filled if supported by
     *     this handler. When returning true, the recipe screen will always display a grayed out
     *     fill button in all contexts. Useful for recipe handlers which support nearly every
     *     recipe, and do not want to pollute the recipe screen.
     */
    @Override
    public boolean alwaysDisplaySupport(EmiRecipe recipe) {
        return true;
    }

    /**
     * @param recipe
     * @param context
     * @return The tooltip describing status for crafting the recipe
     */
    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {
        return EmiRecipeHandler.super.getTooltip(recipe, context);
    }

    /**
     * Render feedback about the status of the current fill. Common use is to render an overlay on
     * missing ingredients
     *
     * @param recipe
     * @param context
     * @param widgets
     * @param draw
     */
    @Override
    public void render(
            EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
        EmiRecipeHandler.super.render(recipe, context, widgets, draw);
    }

    /**
     * @param recipe
     * @param context
     * @return Whether the handler can craft the provided recipe with the given context
     */
    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return true;
    }

    /**
     * @param recipe
     * @param context
     * @return Whether the craft was successful
     */
    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        T menu = context.getScreen().getMenu();
        List<List<GenericStack>> recipeInputs = EmiStackHelper.ofInputs(recipe);
        FakeSlotTransferHelper<T> helper = new FakeSlotTransferHelper<>();
        helper.transfer(menu, recipeInputs);
        return true;
    }
}
