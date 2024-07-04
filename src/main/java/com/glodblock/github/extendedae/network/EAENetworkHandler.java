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
        registerPacket(SExPatternInfo::new);
        registerPacket(SAssemblerAnimation::new);
        registerPacket(SEAEGenericPacket::new);
        registerPacket(CPatternKey::new);
        registerPacket(CUpdatePage::new);
        registerPacket(CEAEGenericPacket::new);
    }

}
