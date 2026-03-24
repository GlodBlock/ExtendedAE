package com.glodblock.github.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.api.orientation.BlockOrientation;
import appeng.init.internal.InitStorageCells;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ExDriveModel implements IUnbakedGeometry<ExDriveModel> {

    private static final ResourceLocation MODEL_BASE = new ResourceLocation("expatternprovider:block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = new ResourceLocation("ae2:block/drive/drive_cell_empty");
    private static final Map<Transformation, Transformation> OPPOSITE = new HashMap<>();

    static {
        for (var dir : Direction.values()) {
            for (int spin = 0; spin < 4; spin++) {
                var oriBlock = BlockOrientation.get(dir, spin);
                var oppositeBlock = BlockOrientation.get(dir.getOpposite(), spin);
                OPPOSITE.put(oriBlock.getTransformation(), oppositeBlock.getTransformation());
            }
        }
    }

    @Nullable
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();
        final Map<Item, BakedModel> invertCellModels = new IdentityHashMap<>();

        final var invertTrans = this.invertTransform(modelTransform);

        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), modelTransform, spriteGetter);
            cellModels.put(entry.getKey(), cellModel);
        }
        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), invertTrans, spriteGetter);
            invertCellModels.put(entry.getKey(), cellModel);
        }

        final BakedModel baseModel = baker.bake(MODEL_BASE, modelTransform, spriteGetter);
        final BakedModel defaultCell = baker.bake(StorageCellModels.getDefaultModel(), modelTransform, spriteGetter);
        final BakedModel invertDefaultCell = baker.bake(StorageCellModels.getDefaultModel(), invertTrans, spriteGetter);
        cellModels.put(Items.AIR, baker.bake(MODEL_CELL_EMPTY, modelTransform, spriteGetter));

        return new ExDriveBakedModel(modelTransform.getRotation(), invertTrans.getRotation(), baseModel, cellModels, invertCellModels, defaultCell, invertDefaultCell);
    }

    private Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.<ResourceLocation>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }

    private ModelState invertTransform(ModelState modelState) {
        var trans = modelState.getRotation();
        var lock = modelState.isUvLocked();
        return new SimpleModelState(OPPOSITE.getOrDefault(trans, trans), lock);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        for (ResourceLocation dependency : getDependencies()) {
            modelGetter.apply(dependency).resolveParents(modelGetter);
        }
    }

    public static class Loader implements IGeometryLoader<ExDriveModel> {

        @Override
        public ExDriveModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ExDriveModel();
        }

    }

}
