package com.glodblock.github.extendedae.xmod.jade;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class JadeDateSender implements IServerDataProvider<BlockAccessor> {

    static final JadeDateSender INSTANCE = new JadeDateSender();

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        CompoundTag jade = new CompoundTag();
        BlockEntity tile = accessor.getBlockEntity();
        if (tile instanceof JadeDataProvider provider) {
            var holder = new CompoundTag();
            provider.collectJadeInfo(holder);
            jade.put(provider.jadeID(), holder);
        }
        if (!jade.isEmpty()) {
            data.put(ExtendedAE.MODID, jade);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ExtendedAE.id("tile_data");
    }

}
