package com.github.glodblock.extendedae.xmod.wt;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.Platform;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wut.WUTHandler;

public class WTCommonLoad {

    public static void container() {
        Platform.registerMenuType("u_wireless_ex_pattern_access_terminal", ContainerUWirelessExPAT.TYPE);
    }

    public static void init() {
        WUTHandler.addTerminal(
                "ex_pattern_access",
                ((IUniversalWirelessTerminalItem) EAEItemAndBlock.WIRELESS_EX_PAT)::tryOpen,
                HostUWirelessExPAT::new,
                ContainerUWirelessExPAT.TYPE,
                (IUniversalWirelessTerminalItem) EAEItemAndBlock.WIRELESS_EX_PAT,
                "wireless_pattern_access_terminal",
                "item.extendedae.wireless_ex_pat"
        );
        Upgrades.add(AE2wtlib.QUANTUM_BRIDGE_CARD, EAEItemAndBlock.WIRELESS_EX_PAT, 1, GuiText.WirelessTerminals.getTranslationKey());
    }

}
