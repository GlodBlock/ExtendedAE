package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.api.CanerMode;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCaner extends AEBaseScreen<ContainerCaner> {

    private final CycleEPPButton modeBtn;

    public GuiCaner(ContainerCaner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.modeBtn = new CycleEPPButton();
        this.modeBtn.addActionPair(EPPIcon.FILLED, Component.translatable("gui.expatternprovider.caner.fill"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.EMPTY.ordinal())));
        this.modeBtn.addActionPair(EPPIcon.BUCKET, Component.translatable("gui.expatternprovider.caner.empty"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.FILL.ordinal())));
        addToLeftToolbar(this.modeBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.modeBtn.setState(menu.getMode().ordinal());
    }
}
