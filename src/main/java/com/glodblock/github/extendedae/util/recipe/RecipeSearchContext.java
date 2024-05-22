package com.glodblock.github.extendedae.util.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class RecipeSearchContext<C extends Container, T extends Recipe<C>> {

    public boolean stuck;
    public boolean dirty;
    @Nullable
    public RecipeHolder<T> lastRecipe;
    @Nullable
    public RecipeHolder<T> currentRecipe;
    private final Supplier<Level> levelGetter;
    private final RecipeType<T> type;

    public RecipeSearchContext(Supplier<Level> levelGetter, RecipeType<T> type) {
        this.levelGetter = levelGetter;
        this.type = type;
    }

    public void findRecipe() {
        if (lastRecipe != null) {
            if (testRecipe(lastRecipe)) {
                currentRecipe = lastRecipe;
                // wait = false;
                stuck = false;
                return;
            }
            lastRecipe = null;
        }
        stuck = false;
        this.onFind(this.searchRecipe());
    }

    public void onInvChange() {
        stuck = false;
        dirty = true;
    }

    public boolean shouldTick() {
        if (currentRecipe != null) {
            return true;
        }
        return !stuck;
    }

    public void onFind(@Nullable RecipeHolder<T> recipe) {
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

    public RecipeHolder<T> searchRecipe() {
        var level = this.levelGetter.get();
        if (level == null) {
            return null;
        }
        var recipes = level.getRecipeManager().byType(this.type);
        for (var recipe : recipes.values()) {
            if (testRecipe(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public abstract boolean testRecipe(RecipeHolder<T> recipe);

    public abstract void runRecipe(RecipeHolder<T> recipe);

    public void save(CompoundTag tag) {
        var nbt = new CompoundTag();
        if (this.currentRecipe != null) {
            nbt.putString("current", this.currentRecipe.id().toString());
        }
        if (this.lastRecipe != null) {
            nbt.putString("last", this.lastRecipe.id().toString());
        }
        tag.put("recipeCtx", nbt);
    }

    public void load(CompoundTag tag) {
        var level = this.levelGetter.get();
        if (level == null) {
            return;
        }
        var nbt = tag.getCompound("recipeCtx");
        if (nbt.contains("current")) {
            try {
                var id = new ResourceLocation(tag.getString("current"));
                this.currentRecipe = level.getRecipeManager().byType(this.type).get(id);
            } catch (Throwable e) {
                this.currentRecipe = null;
            }
        }
        if (nbt.contains("last")) {
            try {
                var id = new ResourceLocation(tag.getString("last"));
                this.lastRecipe = level.getRecipeManager().byType(this.type).get(id);
            } catch (Throwable e) {
                this.lastRecipe = null;
            }
        }
    }

}
