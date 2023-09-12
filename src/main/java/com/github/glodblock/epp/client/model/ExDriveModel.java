package com.github.glodblock.epp.client.model;

import appeng.api.client.StorageCellModels;
import appeng.init.internal.InitStorageCells;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ExDriveModel implements IUnbakedGeometry<ExDriveModel> {

    private static final ResourceLocation MODEL_BASE = new ResourceLocation(
            "expatternprovider:block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = new ResourceLocation(
            "ae2:block/drive/drive_cell_empty");

    @Nullable
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();

        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), modelTransform, spriteGetter);
            cellModels.put(entry.getKey(), cellModel);
        }

        final BakedModel baseModel = baker.bake(MODEL_BASE, modelTransform, spriteGetter);
        final BakedModel defaultCell = baker.bake(StorageCellModels.getDefaultModel(), modelTransform, spriteGetter);
        cellModels.put(Items.AIR, baker.bake(MODEL_CELL_EMPTY, modelTransform, spriteGetter));

        return new ExDriveBakedModel(baseModel, cellModels, defaultCell);
    }

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.<ResourceLocation>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return getDependencies().stream().map(unbakedModelGetter).flatMap(ubm -> ubm.getMaterials(unbakedModelGetter, unresolvedTextureReferences).stream()).toList();
    }

    public static class Loader implements IGeometryLoader<ExDriveModel> {

        @Override
        public ExDriveModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ExDriveModel();
        }

    }

}
