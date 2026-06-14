package com.glodblock.github.appflux.xmod.jade;

import net.minecraft.resources.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final Identifier[] ENERGY = {
            JadeIds.UNIVERSAL_ENERGY_STORAGE,
            Identifier.fromNamespaceAndPath("modern_industrialization", "machine"),
            Identifier.fromNamespaceAndPath("mekanism", "energy")
    };

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();
            for (var loc : ENERGY) {
                if (JadeBlacklist.shouldRemove(target)) {
                    tooltip.getTooltip().remove(loc);
                }
            }
        });
    }

}
