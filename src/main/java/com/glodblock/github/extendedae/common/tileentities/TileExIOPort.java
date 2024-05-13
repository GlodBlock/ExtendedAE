package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.storage.IOPortBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.util.inv.AppEngInternalInventory;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileExIOPort extends IOPortBlockEntity {

    private final AppEngInternalInventory inputCells;
    private static final int NUMBER_OF_CELL_SLOTS = 6;

    public TileExIOPort(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileExIOPort.class, TileExIOPort::new, EAEItemAndBlock.EX_IO_PORT), pos, blockState);
        this.inputCells = Ae2Reflect.getInputCellInv(this);
        Ae2Reflect.setIOPortUpgrade(this, UpgradeInventories.forMachine(EAEItemAndBlock.EX_IO_PORT, 5, this::saveChanges));
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!this.getMainNode().isActive()) {
            return TickRateModulation.IDLE;
        }

        TickRateModulation ret = TickRateModulation.SLEEP;
        long itemsToMove = 2048;

        switch (this.getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD)) {
            case 1 -> itemsToMove *= 2;
            case 2 -> itemsToMove *= 8;
            case 3 -> itemsToMove *= 32;
            case 4 -> itemsToMove *= 128;
            case 5 -> itemsToMove *= 512;
        }

        var grid = getMainNode().getGrid();
        if (grid == null) {
            return TickRateModulation.IDLE;
        }

        for (int x = 0; x < NUMBER_OF_CELL_SLOTS; x++) {
            var cell = this.inputCells.getStackInSlot(x);

            var cellInv = StorageCells.getCellInventory(cell, null);

            if (cellInv == null) {
                Ae2Reflect.moveSlotInCell(this, x);
                continue;
            }

            if (itemsToMove > 0) {
                itemsToMove = Ae2Reflect.transferItemsFromCell(this, grid, cellInv, itemsToMove);

                if (itemsToMove > 0) {
                    ret = TickRateModulation.IDLE;
                } else {
                    ret = TickRateModulation.URGENT;
                }
            }

            if (itemsToMove > 0 && matchesFullnessMode(cellInv) && Ae2Reflect.moveSlotInCell(this, x)) {
                ret = TickRateModulation.URGENT;
            }
        }
        return ret;
    }

}
