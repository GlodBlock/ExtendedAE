package com.github.glodblock.eae.coremod;

import appeng.api.storage.ICellHandler;
import appeng.core.features.registries.cell.CreativeCellHandler;
import com.github.glodblock.eae.handler.InfinityCellHandler;

@SuppressWarnings("unused")
public final class CoreHooks {

    public static boolean isCreativeHandler(ICellHandler cellHandler) {
        return cellHandler instanceof CreativeCellHandler || cellHandler instanceof InfinityCellHandler;
    }

}
