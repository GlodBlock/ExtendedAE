package com.glodblock.github.appflux.common.me.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.core.localization.Tooltips;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GTEUCellHandler implements ICellHandler {

    public static final GTEUCellHandler HANDLER = new GTEUCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.getItem() instanceof IFluxCell cell && cell.getEnergyType() == EnergyType.GTEU;
    }

    @Override
    public @Nullable FluxCellInventory getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        if (isCell(is)) {
            return new GTEUCellInventory((IFluxCell) is.getItem(), is, host);
        }
        return null;
    }

    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        var handler = getCellInventory(is, null);
        if (handler == null) {
            return;
        }
        lines.add(Tooltips.bytesUsed(handler.getUsedBytes(), handler.getTotalBytes()));
        lines.add(Component.translatable("appflux.cell.storage", Tooltips.ofNumber(handler.storedEnergy), handler.getEnergyType().translate()));
    }

}
