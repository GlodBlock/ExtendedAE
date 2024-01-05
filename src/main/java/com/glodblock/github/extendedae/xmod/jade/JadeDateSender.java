package com.glodblock.github.extendedae.xmod.jade;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class JadeDateSender implements IServerDataProvider<BlockAccessor> {

    static final JadeDateSender INSTANCE = new JadeDateSender();

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        CompoundTag epp = new CompoundTag();
        BlockEntity tile = accessor.getBlockEntity();
        if (tile instanceof TileWirelessConnector connector) {
            CompoundTag color = new CompoundTag();
            color.putString("color", connector.getColor().name());
            epp.put("wireless", color);
        }
        if (!epp.isEmpty()) {
            data.put(ExtendedAE.MODID, epp);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ExtendedAE.id("tile_data");
    }
}
