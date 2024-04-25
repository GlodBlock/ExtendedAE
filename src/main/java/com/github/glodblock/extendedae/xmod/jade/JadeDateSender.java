package com.github.glodblock.extendedae.xmod.jade;

import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.tileentities.TileCrystalFixer;
import com.github.glodblock.extendedae.common.tileentities.TileWirelessConnector;
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
        } else if (tile instanceof TileCrystalFixer tgc) {
            CompoundTag state = new CompoundTag();
            state.putInt("progress", tgc.getProgress());
            epp.put("state",state);
        }
        if (!epp.isEmpty()) {
            data.put(EAE.MODID, epp);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return EAE.id("tile_data");
    }
}
