package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonLoad {

    public static void container() {
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("u_wireless_ex_pattern_access_terminal"), ContainerUWirelessExPAT.TYPE);
    }

    public static void init() {
        WUTHandler.addTerminal(
                "ex_pattern_access",
                ((IUniversalWirelessTerminalItem) EPPItemAndBlock.WIRELESS_EX_PAT)::tryOpen,
                HostUWirelessExPAT::new,
                ContainerUWirelessExPAT.TYPE,
                (IUniversalWirelessTerminalItem) EPPItemAndBlock.WIRELESS_EX_PAT,
                "wireless_terminal",
                "item.expatternprovider.wireless_ex_pat"
        );
        Upgrades.add(AE2wtlib.QUANTUM_BRIDGE_CARD, EPPItemAndBlock.WIRELESS_EX_PAT, 1, GuiText.WirelessTerminals.getTranslationKey());
    }

}
