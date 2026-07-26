package com.glodblock.github.extendedae.xmod.jei;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.client.integrations.jei.GenericEntryStackHelper;
import appeng.client.integrations.jei.transfer.AbstractTransferHandler;
import appeng.menu.implementations.UpgradeableMenu;

import com.glodblock.github.extendedae.util.FakeSlotTransferHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;

// Note: this class is also in a PR to base AE2 at https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/8915
// Should that PR be accepted, it will no longer be necessary in ExtendedAE.
public class FakeSlotTransferHandler<T extends UpgradeableMenu<? extends IUpgradeableObject>> extends AbstractTransferHandler implements IUniversalRecipeTransferHandler<@NotNull T> {

    private final Class<T> menuClass;

    public FakeSlotTransferHandler(MenuType<@NotNull T> menuType, Class<T> menuClass, IRecipeTransferHandlerHelper helper){
        this.menuClass = menuClass;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(T menu, @NotNull Object recipeBase, @NotNull IRecipeSlotsView slotsView, @NotNull Player player, boolean maxTransfer, boolean doTransfer){
        if (doTransfer) {
            var recipeInputs = GenericEntryStackHelper.ofInputs(slotsView);
            FakeSlotTransferHelper<T> helper = new FakeSlotTransferHelper<>();
            helper.transfer(menu, recipeInputs);
        }
        return null;
    }

    // Returning empty means the handler will not lock itself to a single MenuType from any given container class.
    @Override
    public @NotNull Optional<MenuType<@NotNull T>> getMenuType() {
        return Optional.empty();
    }

    @Override
    public @NotNull Class<? extends T> getContainerClass() {
        return menuClass;
    }
}