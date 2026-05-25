package com.glodblock.github.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.api.orientation.BlockOrientation;
import appeng.init.internal.InitStorageCells;
import com.glodblock.github.extendedae.ExtendedAE;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class ExDriveModel implements IUnbakedGeometry<ExDriveModel> {

    private static final Identifier MODEL_BASE = ExtendedAE.id("block/extended_drive/extended_me_drive_base");
    private static final Identifier MODEL_CELL_EMPTY = Identifier.parse("ae2:block/drive/drive_cell_empty");
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

    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelTransform, @NotNull ItemOverrides overrides) {
        var invertTrans = this.invertTransform(modelTransform);
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();
        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), modelTransform, spriteGetter);
            cellModels.put(entry.getKey(), cellModel);
        }
        final Map<Item, BakedModel> invertCellModels = new IdentityHashMap<>();
        for (var entry : StorageCellModels.models().entrySet()) {
            var cellModel = baker.bake(entry.getValue(), invertTrans, spriteGetter);
            invertCellModels.put(entry.getKey(), cellModel);
        }
        final BakedModel defaultCell = baker.bake(StorageCellModels.getDefaultModel(), modelTransform, spriteGetter);
        final BakedModel invertDefaultCell = baker.bake(StorageCellModels.getDefaultModel(), invertTrans, spriteGetter);
        final BakedModel baseModel = baker.bake(MODEL_BASE, modelTransform, spriteGetter);
        cellModels.put(Items.AIR, baker.bake(MODEL_CELL_EMPTY, modelTransform, spriteGetter));
        return new ExDriveBakedModel(modelTransform.getRotation(), invertTrans.getRotation(), baseModel, cellModels, invertCellModels, defaultCell, invertDefaultCell);
    }

    public Collection<Identifier> getDependencies() {
        return ImmutableSet.<Identifier>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }

    @Override
    public void resolveParents(@NotNull Function<Identifier, UnbakedModel> modelGetter, @NotNull IGeometryBakingContext context) {
        for (Identifier dependency : getDependencies()) {
            modelGetter.apply(dependency).resolveParents(modelGetter);
        }
    }

    private ModelState invertTransform(ModelState modelState) {
        var trans = modelState.getRotation();
        var lock = modelState.isUvLocked();
        return new SimpleModelState(OPPOSITE.getOrDefault(trans, trans), lock);
    }

    public static class Loader implements IGeometryLoader<ExDriveModel> {

        @Override
        public @NotNull ExDriveModel read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ExDriveModel();
        }

    }

}
