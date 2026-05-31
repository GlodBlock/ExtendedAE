package com.glodblock.github.extendedae.xmod.jade;

import appeng.api.util.AEColor;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class WirelessConnectorTooltip implements IBlockComponentProvider {

    static final WirelessConnectorTooltip INSTANCE = new WirelessConnectorTooltip();

    @Override
    public void appendTooltip(@NotNull ITooltip tooltip, BlockAccessor accessor, @NotNull IPluginConfig iPluginConfig) {
        var target = accessor.getServerData();
        if (target.contains(ExtendedAE.MODID)) {
            target.getCompound(ExtendedAE.MODID).ifPresent(data -> {
                var color = data.getCompoundOrEmpty("wireless").getStringOr("color", "");
                var used = data.getCompoundOrEmpty("wireless").getIntOr("used", 0);
                var aeColor = AEColor.valueOf(color);
                if (aeColor != AEColor.TRANSPARENT) {
                    tooltip.add(Component.translatable(
                                    "jade.wireless_connector.color",
                                    Component.translatable(aeColor.toString())
                            ));
                }
                tooltip.add(Component.translatable(
                                "jade.wireless_connector.used",
                                used
                        ));
            });
        }
    }

    @Override
    public @NotNull Identifier getUid() {
        return ExtendedAE.id("jade_wireless");
    }
}
