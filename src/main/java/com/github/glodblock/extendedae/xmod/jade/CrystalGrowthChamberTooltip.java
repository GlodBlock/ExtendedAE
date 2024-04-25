package com.github.glodblock.extendedae.xmod.jade;

import com.github.glodblock.extendedae.EAE;
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
        if (target.contains(EAE.MODID)) {
            var data = target.getCompound(EAE.MODID);
            if (data.contains("state")) {
                var progress = data.getCompound("state").getInt("progress");
                tooltip.add(Component.translatable("jade.crystal_chamber.progress", progress));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return EAE.id("jade_chamber");
    }
}
