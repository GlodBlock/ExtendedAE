package com.github.glodblock.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.github.glodblock.extendedae.api.CanerMode;
import com.github.glodblock.extendedae.client.button.CycleEPPButton;
import com.github.glodblock.extendedae.client.button.EPPIcon;
import com.github.glodblock.extendedae.container.ContainerCaner;
import com.github.glodblock.extendedae.network.EAENetworkServer;
import com.github.glodblock.extendedae.network.packet.CGenericPacket;
import com.github.glodblock.extendedae.network.packet.sync.IActionHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class GuiCaner extends AEBaseScreen<ContainerCaner> implements IActionHolder {

    private final Map<String, Consumer<Object[]>> actions = new Object2ObjectOpenHashMap<>();

    public GuiCaner(ContainerCaner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        var modeBtn = new CycleEPPButton();
        modeBtn.addActionPair(EPPIcon.FILLED, Component.translatable("gui.extendedae.caner.fill"), b -> EAENetworkServer.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.EMPTY.ordinal())));
        modeBtn.addActionPair(EPPIcon.BUCKET, Component.translatable("gui.extendedae.caner.empty"), b -> EAENetworkServer.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.FILL.ordinal())));
        this.actions.put("init", o -> modeBtn.setState((Integer) o[0]));
        EAENetworkServer.INSTANCE.sendToServer(new CGenericPacket("update"));
        addToLeftToolbar(modeBtn);
    }

    @NotNull
    @Override
    public Map<String, Consumer<Object[]>> getActionMap() {
        return this.actions;
    }
}
