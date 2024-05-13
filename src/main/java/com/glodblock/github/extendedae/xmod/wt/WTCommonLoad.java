package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class WTCommonLoad {

    public static void container() {
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("u_wireless_ex_pattern_access_terminal"), ContainerUWirelessExPAT.TYPE);
    }

    public static void init() {
        WUTHandler.addTerminal(
                "ex_pattern_access",
                ((ItemWT) EAEItemAndBlock.WIRELESS_EX_PAT)::tryOpen,
                HostUWirelessExPAT::new,
                ContainerUWirelessExPAT.TYPE,
                (ItemWT) EAEItemAndBlock.WIRELESS_EX_PAT,
                "wireless_pattern_access_terminal",
                "item.expatternprovider.wireless_ex_pat"
        );
        Upgrades.add(AE2wtlibItems.instance().QUANTUM_BRIDGE_CARD, EAEItemAndBlock.WIRELESS_EX_PAT, 1, GuiText.WirelessTerminals.getTranslationKey());
    }

}
