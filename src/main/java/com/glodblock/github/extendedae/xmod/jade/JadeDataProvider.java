package com.glodblock.github.extendedae.xmod.jade;

import net.minecraft.nbt.CompoundTag;

public interface JadeDataProvider {

    String jadeID();

    void collectJadeInfo(CompoundTag tag);

}
