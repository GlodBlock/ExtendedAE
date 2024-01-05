package com.github.glodblock.epp.network;

import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.network.packet.CPatternKey;
import com.github.glodblock.epp.network.packet.CUpdatePage;
import com.github.glodblock.epp.network.packet.SAssemblerAnimation;
import com.github.glodblock.epp.network.packet.SExPatternInfo;
import com.glodblock.github.glodium.network.NetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.SGenericPacket;

public class EPPNetworkHandler extends NetworkHandler {

    public static final EPPNetworkHandler INSTANCE = new EPPNetworkHandler();

    public EPPNetworkHandler() {
        super(EPP.MODID);
    }

    public void init() {
        registerPacket(SExPatternInfo.class, SExPatternInfo::new);
        registerPacket(SAssemblerAnimation.class, SAssemblerAnimation::new);
        registerPacket(SGenericPacket.class, SGenericPacket::new);
        registerPacket(CPatternKey.class, CPatternKey::new);
        registerPacket(CUpdatePage.class, CUpdatePage::new);
        registerPacket(CGenericPacket.class, CGenericPacket::new);
    }

}
