package com.glodblock.github.appflux.xmod.jade;

import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final ResourceLocation[] ENERGY = {
            new ResourceLocation("energy_storage"),
            new ResourceLocation("gtceu", "electric_container_provider")
    };

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();
            for (var loc : ENERGY) {
                if (target instanceof InterfaceLogicHost ||
                        target instanceof PatternProviderLogicHost ||
                        target instanceof TileFluxAccessor) {
                    tooltip.remove(loc);
                }
            }
        });
    }

}
