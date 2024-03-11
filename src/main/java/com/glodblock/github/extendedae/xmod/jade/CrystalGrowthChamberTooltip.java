package com.glodblock.github.extendedae.xmod.jade;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CrystalGrowthChamberTooltip implements IBlockComponentProvider {
    static final CrystalGrowthChamberTooltip INSTANCE = new CrystalGrowthChamberTooltip();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        var target = accessor.getServerData();
        if (target.contains(ExtendedAE.MODID)) {
            var data = target.getCompound(ExtendedAE.MODID);
            if (data.contains("state")) {
                var progress = data.getCompound("state").getInt("progress");
                tooltip.add(Component.translatable("jade.crystal_chamber.progress", progress));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ExtendedAE.id("jade_chamber");
    }
}
