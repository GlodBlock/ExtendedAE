package com.glodblock.github.extendedae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.xmod.jade.JadeBlacklist;
import com.glodblock.github.appflux.xmod.wc.ChargerBlacklist;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.xmod.ThirdParty;
import com.glodblock.github.glodium.xmod.XModLoader;

@ThirdParty(ModConstants.APPFLUX)
public class AppFluxLoader implements XModLoader  {

    @Override
    public String modid() {
        return ModConstants.APPFLUX;
    }

    @Override
    public void loadCommon() {
        try {
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_PATTERN_PROVIDER, 1, "block.extendedae.ex_pattern_provider");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_PATTERN_PROVIDER_PART, 1, "block.extendedae.ex_pattern_provider");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
            ChargerBlacklist.BLACKLIST.add(te -> te instanceof TileIngredientBuffer);
            JadeBlacklist.BLACK_LIST.add(o -> o instanceof TileIngredientBuffer);
            JadeBlacklist.BLACK_LIST.add(o -> o instanceof TileCaner);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

    @Override
    public void loadClient() {

    }

    @Override
    public void onRegister(RegistryHandler handler) {

    }

}
