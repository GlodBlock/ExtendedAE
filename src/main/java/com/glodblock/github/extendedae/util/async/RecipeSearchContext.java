package com.glodblock.github.extendedae.util.async;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeSearchContext<T extends Recipe<?>> {

    public boolean wait;
    public boolean stuck;
    public boolean dirty;
    @Nullable
    public RecipeHolder<T> lastRecipe;
    @Nullable
    public RecipeHolder<T> currentRecipe;

    public void findRecipe() {
        if (lastRecipe != null) {
            if (testRecipe(lastRecipe)) {
                currentRecipe = lastRecipe;
                wait = false;
                stuck = false;
                return;
            }
            lastRecipe = null;
        }
        stuck = false;
        if (!wait) {
            ThreadCenter.requestSearch(this::searchRecipe, this::onFind);
        }
        wait = true;
    }

    public void onInvChange() {
        stuck = false;
        dirty = true;
    }

    public boolean shouldTick() {
        if (currentRecipe != null) {
            return true;
        }
        return !wait && !stuck;
    }

    public void onFind(@Nullable RecipeHolder<T> recipe) {
        wait = false;
        if (recipe == null) {
            if (dirty) {
                dirty = false;
                return;
            }
            stuck = true;
            currentRecipe = null;
            return;
        }
        dirty = false;
        lastRecipe = recipe;
        currentRecipe = recipe;
        stuck = false;
    }

    public void init() {
        wait = false;
        stuck = false;
        dirty = false;
        lastRecipe = null;
        currentRecipe = null;
    }

    public abstract RecipeHolder<T> searchRecipe();

    public abstract boolean testRecipe(RecipeHolder<T> recipe);

    public abstract void runRecipe(RecipeHolder<T> recipe);

}
