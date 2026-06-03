package com.glodblock.github.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.client.model.SpinnableVariant;
import com.glodblock.github.extendedae.ExtendedAE;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.Map;

public record ExDriveModel(SpinnableVariant variant, SpinnableVariant.SimpleModelState opposite) implements CustomUnbakedBlockStateModel {

    public static MapCodec<ExDriveModel> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SpinnableVariant.MAP_CODEC.forGetter(ExDriveModel::variant),
            Quadrant.CODEC.optionalFieldOf("extended:x", Quadrant.R0).forGetter(model -> model.opposite.x()),
            Quadrant.CODEC.optionalFieldOf("extended:y", Quadrant.R0).forGetter(model -> model.opposite.y()),
            Quadrant.CODEC.optionalFieldOf("extended:z", Quadrant.R0).forGetter(model -> model.opposite.z()))
            .apply(builder, ExDriveModel::create)
    );
    private static final Identifier MODEL_BASE = ExtendedAE.id("block/extended_drive/extended_me_drive_base");
    private static final Transformation[] TRANSFORMATIONS = buildSlotTransforms();

    private static ExDriveModel create(SpinnableVariant variant, Quadrant x, Quadrant y, Quadrant z) {
        return new ExDriveModel(variant, new SpinnableVariant.SimpleModelState(x, y, z, false));
    }

    @Override
    public @NotNull BlockStateModel bake(@NotNull ModelBaker baker) {
        final Map<Item, BlockStateModelPart[]> cellModels = new IdentityHashMap<>();
        var modelState = variant.modelState().asModelState();
        var oppoState = opposite.asModelState();
        ModelState[] bayTransforms = new ModelState[TRANSFORMATIONS.length];
        for (int i = 0; i < TRANSFORMATIONS.length; i++) {
            bayTransforms[i] = new ComposedModelState(i >= 10 ? oppoState : modelState, TRANSFORMATIONS[i]);
        }
        for (var entry : StorageCellModels.models().entrySet()) {
            var location = entry.getValue();
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                var slots = baker.getModel(location).getTopTextureSlots();
                if (slots.getMaterial("particle") == null) {
                    ExtendedAE.LOGGER.error("Storage cell model {} is missing a 'particle' texture", location);
                }
            }
            cellModels.put(entry.getKey(), preBakeCellInBays(baker, bayTransforms, location));
        }
        var defaultCells = preBakeCellInBays(baker, bayTransforms, StorageCellModels.getDefaultModel());
        var baseModel = SimpleModelWrapper.bake(baker, MODEL_BASE, modelState);
        return new ExDriveBakedModel(baseModel, cellModels, defaultCells);
    }

    private BlockStateModelPart[] preBakeCellInBays(ModelBaker baker, ModelState[] bayTransforms, Identifier location) {
        // Bake each cell pre-translated into each of the bays
        var cellsInBay = new BlockStateModelPart[TRANSFORMATIONS.length];
        for (int i = 0; i < bayTransforms.length; i++) {
            cellsInBay[i] = SimpleModelWrapper.bake(baker, location, bayTransforms[i]);
        }
        return cellsInBay;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        resolver.markDependency(MODEL_BASE);
        resolver.markDependency(StorageCellModels.getDefaultModel());
        StorageCellModels.models().values().forEach(resolver::markDependency);
    }

    private static Transformation[] buildSlotTransforms() {
        Transformation[] transforms = new Transformation[20];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                Vector3f translation = new Vector3f();
                getSlotOrigin(row, col, translation);
                transforms[ExDriveBakedModel.getSlotIndex(row, col, 0)] = new Transformation(translation, null, null, null);
            }
        }
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                Vector3f translation = new Vector3f();
                getSlotOrigin(row, col, translation);
                transforms[ExDriveBakedModel.getSlotIndex(row, col, 1)] = new Transformation(translation, null, null, null);
            }
        }
        return transforms;
    }

    public static void getSlotOrigin(int row, int col, Vector3f translation) {
        // Position this drive model copy at the correct slot. The transform is based on
        // the cell-model being in slot 0,0,0 while the upper left slot's origin is at
        // 9,13,1
        float xOffset = (9 - col * 8) / 16.0f;
        float yOffset = (13 - row * 3) / 16.0f;
        translation.set(xOffset, yOffset, 1 / 16.0f);
    }

    @Override
    public @NotNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return CODEC;
    }

}
