package com.glodblock.github.extendedae.client.model;

import appeng.block.storage.DriveModelData;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ExDriveBakedModel implements DynamicBlockStateModel {

    private final BlockStateModelPart baseModel;
    private final Map<Item, BlockStateModelPart[]> cellModels;
    private final BlockStateModelPart[] defaultCellModel;

    public ExDriveBakedModel(BlockStateModelPart baseModel, Map<Item, BlockStateModelPart[]> cellModels, BlockStateModelPart[] defaultCell) {
        this.baseModel = baseModel;
        this.defaultCellModel = defaultCell;
        this.cellModels = cellModels;
    }

    @Override
    public void collectParts(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull RandomSource rand, @NotNull List<BlockStateModelPart> parts) {
        parts.add(this.baseModel);
        var cells = level.getModelData(pos).get(DriveModelData.STATE);
        // Add cell models on top of the base model, if possible
        if (cells != null) {
            for (int disk = 0; disk < 2; disk++) {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 2; col++) {
                        int slot = getSlotIndex(row, col, disk);
                        // Add the cell chassis
                        Item cell = slot < cells.length ? cells[slot] : null;
                        var cellChassisModel = getCellChassisModel(cell, getSlotIndex(row, col, disk));
                        if (cellChassisModel != null && cell != Items.AIR) {
                            parts.add(cellChassisModel);
                        }
                    }
                }
            }
        }
    }

    // Determine which drive chassis to show based on the used cell
    public BlockStateModelPart getCellChassisModel(Item cell, int index) {
        if (cell == null) {
            return null;
        }
        final BlockStateModelPart[] model = cellModels.get(cell);
        if (model != null) {
            return model[index];
        }
        return this.defaultCellModel[index];
    }

    public static int getSlotIndex(int row, int col, int disk) {
        return row * 2 + col + disk * 10;
    }

    @Override
    public Material.@NotNull Baked particleMaterial() {
        return baseModel.particleMaterial();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return baseModel.materialFlags();
    }

}
