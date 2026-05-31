package com.glodblock.github.extendedae.client.model;

import appeng.api.client.StorageCellModels;
import appeng.api.orientation.BlockOrientation;
import appeng.client.model.SpinnableVariant;
import com.glodblock.github.extendedae.ExtendedAE;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.ComposedModelState;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public record ExDriveModel(SpinnableVariant variant) implements CustomUnbakedBlockStateModel {

    public static MapCodec<ExDriveModel> CODEC = SpinnableVariant.MAP_CODEC.xmap(ExDriveModel::new, ExDriveModel::variant);
    private static final Identifier MODEL_BASE = ExtendedAE.id("block/extended_drive/extended_me_drive_base");
    private static final Map<Transformation, Transformation> OPPOSITE = new HashMap<>();
    private static final Transformation[] TRANSFORMATIONS = buildSlotTransforms();

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
    public @NotNull BlockStateModel bake(@NotNull ModelBaker baker) {
        final Map<Item, BlockStateModelPart[]> cellModels = new IdentityHashMap<>();
        var modelState = variant.modelState().asModelState();
        ModelState[] bayTransforms = new ModelState[TRANSFORMATIONS.length];
        for (int i = 0; i < TRANSFORMATIONS.length; i++) {
            bayTransforms[i] = new ComposedModelState(modelState, applySpinTransformation(modelState, TRANSFORMATIONS[i], i >= 10));
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

    private Transformation applySpinTransformation(ModelState model, Transformation origin, boolean state) {
        if (state) {
            var trans = model.transformation();
            return OPPOSITE.getOrDefault(trans, trans).compose(origin);
        }
        return origin;
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
