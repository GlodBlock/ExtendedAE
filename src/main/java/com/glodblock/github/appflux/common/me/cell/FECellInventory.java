package com.glodblock.github.appflux.common.me.cell;

import appeng.api.storage.cells.ISaveProvider;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FECellInventory extends FluxCellInventory {

    public FECellInventory(IFluxCell cellType, ItemStack o, @Nullable ISaveProvider container) {
        super(cellType, o, container);
    }

    @Override
    protected EnergyType getEnergyType() {
        return EnergyType.FE;
    }
}
