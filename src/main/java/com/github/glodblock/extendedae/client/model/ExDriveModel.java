package com.github.glodblock.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.client.render.BasicUnbakedModel;
import appeng.init.internal.InitStorageCells;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ExDriveModel implements BasicUnbakedModel {

    private static final ResourceLocation MODEL_BASE = new ResourceLocation(
            "extendedae:block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = new ResourceLocation(
            "ae2:block/drive/drive_cell_empty");

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();
        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), modelTransform);
            cellModels.put(entry.getKey(), cellModel);
        }
        final BakedModel baseModel = baker.bake(MODEL_BASE, modelTransform);
        final BakedModel defaultCell = baker.bake(StorageCellModels.getDefaultModel(), modelTransform);
        cellModels.put(Items.AIR, baker.bake(MODEL_CELL_EMPTY, modelTransform));
        return new ExDriveBakedModel(modelTransform.getRotation(), baseModel, cellModels, defaultCell);
    }

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.<ResourceLocation>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }

}
