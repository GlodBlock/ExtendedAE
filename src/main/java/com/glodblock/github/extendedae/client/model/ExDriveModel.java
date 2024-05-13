package com.glodblock.github.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.init.internal.InitStorageCells;
import com.glodblock.github.extendedae.ExtendedAE;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ExDriveModel implements IUnbakedGeometry<ExDriveModel> {

    private static final ResourceLocation MODEL_BASE = ExtendedAE.id("block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = new ResourceLocation("ae2:block/drive/drive_cell_empty");

    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelTransform, @NotNull ItemOverrides overrides, @NotNull ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();

        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), modelTransform, spriteGetter);
            cellModels.put(entry.getKey(), cellModel);
        }

        final BakedModel baseModel = baker.bake(MODEL_BASE, modelTransform, spriteGetter);
        final BakedModel defaultCell = baker.bake(StorageCellModels.getDefaultModel(), modelTransform, spriteGetter);
        cellModels.put(Items.AIR, baker.bake(MODEL_CELL_EMPTY, modelTransform, spriteGetter));

        return new ExDriveBakedModel(modelTransform.getRotation(), baseModel, cellModels, defaultCell);
    }

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.<ResourceLocation>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }

    @Override
    public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> modelGetter, @NotNull IGeometryBakingContext context) {
        for (ResourceLocation dependency : getDependencies()) {
            modelGetter.apply(dependency).resolveParents(modelGetter);
        }
    }

    public static class Loader implements IGeometryLoader<ExDriveModel> {

        @Override
        public @NotNull ExDriveModel read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ExDriveModel();
        }

    }

}
