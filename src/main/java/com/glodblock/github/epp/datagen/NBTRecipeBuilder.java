package com.glodblock.github.epp.datagen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NBTRecipeBuilder implements CraftingRecipeJsonBuilder {
    private final ItemStack output;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> inputs = Maps.newLinkedHashMap();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
    @Nullable
    private String group;

    public NBTRecipeBuilder(ItemStack output) {
        this.output = output;
    }

    public static NBTRecipeBuilder create(ItemConvertible output) {
        return create(output, 1);
    }

    public static NBTRecipeBuilder create(ItemConvertible output, int outputCount) {
        return create(new ItemStack(output, outputCount));
    }

    public static NBTRecipeBuilder create(ItemStack output) {
        return new NBTRecipeBuilder(output);
    }

    public NBTRecipeBuilder input(Character c, TagKey<Item> tag) {
        return this.input(c, Ingredient.fromTag(tag));
    }

    public NBTRecipeBuilder input(Character c, ItemStack stack) {
        return this.input(c, Ingredient.ofStacks(stack));
    }

    public NBTRecipeBuilder input(Character c, ItemConvertible itemProvider) {
        return this.input(c, Ingredient.ofItems(itemProvider));
    }

    public NBTRecipeBuilder input(Character c, Ingredient ingredient) {
        if (this.inputs.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        }
        if (c == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.inputs.put(c, ingredient);
        return this;
    }

    public NBTRecipeBuilder pattern(String patternStr) {
        if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.pattern.add(patternStr);
        return this;
    }

    @Override
    public NBTRecipeBuilder criterion(String string, CriterionConditions criterionConditions) {
        this.advancementBuilder.criterion(string, criterionConditions);
        return this;
    }

    @Override
    public NBTRecipeBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output.getItem();
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        assert this.output.getItem().getGroup() != null;
        exporter.accept(new NBTRecipeBuilder.ShapedRecipeJsonProvider(recipeId, this.output, this.group == null ? "" : this.group, this.pattern, this.inputs, this.advancementBuilder, new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getItem().getGroup().getName() + "/" + recipeId.getPath())));
    }

    private void validate(Identifier recipeId) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
        }
        HashSet<Character> set = Sets.newHashSet(this.inputs.keySet());
        set.remove(' ');
        for (String string : this.pattern) {
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (!this.inputs.containsKey(c) && c != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + c + "'");
                }
                set.remove(c);
            }
        }
        if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
        }
        if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
        }
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    static class ShapedRecipeJsonProvider
            implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final ItemStack output;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> inputs;
        private final Advancement.Builder advancementBuilder;
        private final Identifier advancementId;

        public ShapedRecipeJsonProvider(Identifier recipeId, ItemStack output, String group, List<String> pattern, Map<Character, Ingredient> inputs, Advancement.Builder advancementBuilder, Identifier advancementId) {
            this.recipeId = recipeId;
            this.output = output;
            this.group = group;
            this.pattern = pattern;
            this.inputs = inputs;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (String string : this.pattern) {
                jsonArray.add(string);
            }
            json.add("pattern", jsonArray);
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : this.inputs.entrySet()) {
                jsonObject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            json.add("key", jsonObject);
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getId(this.output.getItem()).toString());
            if (this.output.getCount() > 1) {
                jsonObject2.addProperty("count", this.output.getCount());
            }
            if (this.output.hasNbt()) {
                assert this.output.getNbt() != null;
                jsonObject2.addProperty("tag", this.output.getNbt().toString());
            }
            json.add("result", jsonObject2);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPED;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.advancementBuilder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}

