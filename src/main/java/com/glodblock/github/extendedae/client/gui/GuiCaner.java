package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.api.CanerMode;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCaner extends AEBaseScreen<ContainerCaner> {

    private final CycleEPPButton modeBtn;

    public GuiCaner(ContainerCaner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.modeBtn = new CycleEPPButton();
        this.modeBtn.addActionPair(EPPIcon.FILLED, Component.translatable("gui.extendedae.caner.fill"), _ -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", CanerMode.EMPTY)));
        this.modeBtn.addActionPair(EPPIcon.BUCKET, Component.translatable("gui.extendedae.caner.empty"), _ -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", CanerMode.FILL)));
        addToLeftToolbar(this.modeBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.modeBtn.setState(menu.getMode().ordinal());
    }

}
