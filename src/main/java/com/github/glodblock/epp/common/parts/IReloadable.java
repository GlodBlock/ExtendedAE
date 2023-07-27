package com.github.glodblock.epp.common.parts;

import net.minecraft.nbt.CompoundTag;

public interface IReloadable {

    void reloadFromNBT(CompoundTag data);

}
