package com.glodblock.github.appflux.common.me.energy;

import appeng.api.parts.RegisterPartCapabilitiesEvent;
import com.glodblock.github.appflux.common.AFRegistryHandler;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.xmod.ModConstants;
import com.glodblock.github.appflux.xmod.mek.MekEnergyCap;
import com.glodblock.github.appflux.xmod.mi.MIEnergyCap;
import com.glodblock.github.glodium.util.GlodUtil;
import net.neoforged.neoforge.capabilities.Capabilities;

public class CapAdaptor {

    public static void init(AFRegistryHandler event) {
        event.cap(TileFluxAccessor.class, Capabilities.EnergyStorage.BLOCK, (te, side) -> te.getEnergyStorage());
        if (GlodUtil.checkMod(ModConstants.MEK)) {
            event.cap(TileFluxAccessor.class, MekEnergyCap.CAP, (te, side) -> MekEnergyCap.of(te.getStorage(), te.getSource()));
        }
        if (GlodUtil.checkMod(ModConstants.MI) && AFConfig.miSupport()) {
            event.cap(TileFluxAccessor.class, MIEnergyCap.CAP, (te, side) -> MIEnergyCap.of(te.getStorage(), te.getSource()));
        }
    }

    public static void init(RegisterPartCapabilitiesEvent event) {
        event.register(
                Capabilities.EnergyStorage.BLOCK,
                (part, direction) -> part.getEnergyStorage(),
                PartFluxAccessor.class
        );
        if (GlodUtil.checkMod(ModConstants.MEK)) {
            event.register(
                    MekEnergyCap.CAP,
                    (part, direction) -> MekEnergyCap.of(part.getStorage(), part.getSource()),
                    PartFluxAccessor.class
            );
        }
        if (GlodUtil.checkMod(ModConstants.MI) && AFConfig.miSupport()) {
            event.register(
                    MIEnergyCap.CAP,
                    (part, direction) -> MIEnergyCap.of(part.getStorage(), part.getSource()),
                    PartFluxAccessor.class
            );
        }
    }

}
