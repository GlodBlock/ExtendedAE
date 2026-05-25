package com.glodblock.github.extendedae.api.caps;

import appeng.api.storage.MEStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IMEStorageAccess {

    default MEStorage getMEStorage(@Nullable Direction side) {
        return getMEStorage();
    }

    MEStorage getMEStorage();

}
