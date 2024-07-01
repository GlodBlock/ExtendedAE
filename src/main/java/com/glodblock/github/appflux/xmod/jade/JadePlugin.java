package com.glodblock.github.appflux.xmod.jade;

import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final ResourceLocation[] ENERGY = {
            JadeIds.UNIVERSAL_ENERGY_STORAGE,
            ResourceLocation.fromNamespaceAndPath("modern_industrialization", "machine")
    };

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();
            for (var loc : ENERGY) {
                if (target instanceof InterfaceLogicHost ||
                    target instanceof PatternProviderLogicHost ||
                    target instanceof TileFluxAccessor) {
                    tooltip.getTooltip().remove(loc);
                }
            }
        });
    }

}
