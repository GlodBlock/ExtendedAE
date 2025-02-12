package com.glodblock.github.extendedae.api;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IEnergySource;
import com.glodblock.github.glodium.recipe.RecipeSearchContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IRecipeMachine<C extends RecipeInput, T extends Recipe<C>> {

    int getProgress();

    void addProgress(int delta);

    void setProgress(int progress);

    RecipeSearchContext<C, T> getContext();

    void setWorking(boolean work);

    InternalInventory getOutput();

    Set<Direction> getOutputSides();

    @Nullable
    default IManagedGridNode getNode() {
        return null;
    }

    @Nullable
    default IEnergySource getEnergy() {
        return null;
    }

}
