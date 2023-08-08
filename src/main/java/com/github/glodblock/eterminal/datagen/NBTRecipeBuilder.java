package com.github.glodblock.eterminal.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class NBTRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;

    public NBTRecipeBuilder(ItemStack p_251475_) {
        this.result = p_251475_;
    }

    public static NBTRecipeBuilder shaped(ItemLike p_249747_) {
        return shaped(p_249747_, 1);
    }

    public static NBTRecipeBuilder shaped(ItemLike p_250636_, int p_249081_) {
        return shaped(new ItemStack(p_250636_, p_249081_));
    }

    public static NBTRecipeBuilder shaped(ItemStack p_251475_) {
        return new NBTRecipeBuilder(p_251475_);
    }

    public NBTRecipeBuilder define(Character p_206417_, TagKey<Item> p_206418_) {
        return this.define(p_206417_, Ingredient.of(p_206418_));
    }

    public NBTRecipeBuilder define(Character p_126128_, ItemLike p_126129_) {
        return this.define(p_126128_, Ingredient.of(p_126129_));
    }

    public NBTRecipeBuilder define(Character p_126128_, ItemStack p_126129_) {
        return this.define(p_126128_, Ingredient.of(p_126129_));
    }

    public NBTRecipeBuilder define(Character p_126125_, Ingredient p_126126_) {
        if (this.key.containsKey(p_126125_)) {
            throw new IllegalArgumentException("Symbol '" + p_126125_ + "' is already defined!");
        } else if (p_126125_ == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(p_126125_, p_126126_);
            return this;
        }
    }

    public NBTRecipeBuilder pattern(String p_126131_) {
        if (!this.rows.isEmpty() && p_126131_.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(p_126131_);
            return this;
        }
    }

    public @NotNull NBTRecipeBuilder unlockedBy(@NotNull String p_126133_, @NotNull CriterionTriggerInstance p_126134_) {
        this.advancement.addCriterion(p_126133_, p_126134_);
        return this;
    }

    public @NotNull NBTRecipeBuilder group(@Nullable String p_126146_) {
        this.group = p_126146_;
        return this;
    }

    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    public void save(Consumer<FinishedRecipe> p_126141_, @NotNull ResourceLocation p_126142_) {
        this.ensureValid(p_126142_);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_126142_)).rewards(AdvancementRewards.Builder.recipe(p_126142_)).requirements(RequirementsStrategy.OR);
        p_126141_.accept(new NBTRecipeBuilder.Result(p_126142_, this.result, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(p_126142_.getNamespace(), "recipes/" + Objects.requireNonNull(this.result.getItem().getItemCategory()).getRecipeFolderName() + "/" + p_126142_.getPath())));
    }

    private void ensureValid(ResourceLocation p_126144_) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + p_126144_ + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for(String s : this.rows) {
                for(int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + p_126144_ + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + p_126144_);
            } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + p_126144_ + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.advancement.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + p_126144_);
            }
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation p_273548_, ItemStack p_273530_, String p_273549_, List<String> p_273744_, Map<Character, Ingredient> p_272991_, Advancement.Builder p_273260_, ResourceLocation p_273106_) {
            this.id = p_273548_;
            this.result = p_273530_;
            this.group = p_273549_;
            this.pattern = p_273744_;
            this.key = p_272991_;
            this.advancement = p_273260_;
            this.advancementId = p_273106_;
        }

        public void serializeRecipeData(@NotNull JsonObject p_126167_) {
            if (!this.group.isEmpty()) {
                p_126167_.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();

            for(String s : this.pattern) {
                jsonarray.add(s);
            }

            p_126167_.add("pattern", jsonarray);
            JsonObject jsonobject = new JsonObject();

            for(Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            p_126167_.add("key", jsonobject);
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", Registry.ITEM.getKey(this.result.getItem()).toString());
            if (this.result.getDamageValue() > 0) {
                jsonobject1.addProperty("meta", this.result.getDamageValue());
            }
            if (this.result.hasTag()) {
                jsonobject1.addProperty("nbt", this.result.getTag().getAsString());
            }

            if (this.result.getCount() > 1) {
                jsonobject1.addProperty("count", this.result.getCount());
            }

            p_126167_.add("result", jsonobject1);
        }

        public @NotNull RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
        }

        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
