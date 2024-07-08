package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.terminal.Icon;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wut.AddTerminalEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class WTCommonLoad {

    private static final Icon.Texture TX = new Icon.Texture(ExtendedAE.id("textures/guis/nicons.png"), 64, 64);

    public static void container() {
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("u_wireless_ex_pattern_access_terminal"), ContainerUWirelessExPAT.TYPE);
        Upgrades.add(AE2wtlibItems.QUANTUM_BRIDGE_CARD, EAESingletons.WIRELESS_EX_PAT, 1, GuiText.WirelessTerminals.getTranslationKey());
    }

    public static void init() {
        AddTerminalEvent.register(event -> event.builder(
                "ex_pattern_access",
                HostUWirelessExPAT::new,
                ContainerUWirelessExPAT.TYPE,
                (ItemWT) EAESingletons.WIRELESS_EX_PAT,
                new Icon(32, 32, 16, 16, TX)
        ).addTerminal());
    }

}
