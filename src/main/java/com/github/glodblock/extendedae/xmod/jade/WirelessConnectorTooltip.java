package com.github.glodblock.extendedae.xmod.jade;

import appeng.api.util.AEColor;
import com.github.glodblock.extendedae.EAE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class WirelessConnectorTooltip implements IBlockComponentProvider {

    static final WirelessConnectorTooltip INSTANCE = new WirelessConnectorTooltip();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
        var target = accessor.getServerData();
        if (target.contains(EAE.MODID)) {
            var data = target.getCompound(EAE.MODID);
            if (data.contains("wireless")) {
                var color = data.getCompound("wireless").getString("color");
                var aeColor = AEColor.valueOf(color);
                if (aeColor != AEColor.TRANSPARENT) {
                    tooltip.add(
                            Component.translatable(
                                    "jade.wireless_connector.color",
                                    Component.translatable(aeColor.toString())
                            )
                    );
                }
            }

        }
    }

    @Override
    public ResourceLocation getUid() {
        return EAE.id("jade_wireless");
    }
}
