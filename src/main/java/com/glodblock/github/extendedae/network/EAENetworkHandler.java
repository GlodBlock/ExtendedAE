package com.glodblock.github.extendedae.network;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import com.glodblock.github.extendedae.network.packet.CUpdatePage;
import com.glodblock.github.extendedae.network.packet.SAssemblerAnimation;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.extendedae.network.packet.SExPatternInfo;
import com.glodblock.github.glodium.network.NetworkHandler;

public class EAENetworkHandler extends NetworkHandler {

    public static final EAENetworkHandler INSTANCE = new EAENetworkHandler();

    public EAENetworkHandler() {
        super(ExtendedAE.MODID);
        registerPacket(ExtendedAE.id("s_ex_pattern_info"), SExPatternInfo::new);
        registerPacket(ExtendedAE.id("s_assembler_animation"), SAssemblerAnimation::new);
        registerPacket(ExtendedAE.id("s_generic"), SEAEGenericPacket::new);
        registerPacket(ExtendedAE.id("c_pattern_key"), CPatternKey::new);
        registerPacket(ExtendedAE.id("c_update_page"), CUpdatePage::new);
        registerPacket(ExtendedAE.id("c_generic"), CEAEGenericPacket::new);
    }

}
