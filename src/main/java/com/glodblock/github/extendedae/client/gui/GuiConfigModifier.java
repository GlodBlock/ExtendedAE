package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.extendedae.common.items.tools.ItemConfigModifier;
import com.glodblock.github.extendedae.container.ContainerConfigModifier;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class GuiConfigModifier extends AEBaseScreen<ContainerConfigModifier> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private ItemConfigModifier.ConfigSettings.Mode mode = ItemConfigModifier.ConfigSettings.Mode.MUL;
    private long data = 1;
    private final AE2Button changeMode;
    private final AETextField dataInput;
    private static final Pattern NUMBER = Pattern.compile("[0-9]*");

    public GuiConfigModifier(ContainerConfigModifier menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.changeMode = new AE2Button(Component.empty(), b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set_mode", this.mode.getNext().ordinal())));
        this.changeMode.setSize(50, 20);
        this.dataInput = widgets.addTextField("data_input");
        this.dataInput.setMaxLength(15);
        this.dataInput.setFilter(NUMBER.asMatchPredicate());
        this.dataInput.setPlaceholder(Component.translatable("gui.extendedae.config_modifier.data_input"));
        this.dataInput.setResponder(this::syncData);
        this.actions.put("init", o -> setMode(o.get(0), o.get(1)));
        EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("update"));
    }

    private void setMode(int mode, long data) {
        this.mode = ItemConfigModifier.ConfigSettings.Mode.values()[mode];
        this.data = data;
        this.dataInput.setValue(String.valueOf(data));
    }

    private void syncData(String data) {
        try {
            long sync = Long.parseLong(data);
            EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set_data", sync));
        } catch (Exception ignore) {
            // NO-OP
        }
    }

    @Override
    public void init() {
        super.init();
        this.changeMode.setPosition(this.leftPos + 12, this.topPos + 22);
        this.addRenderableWidget(this.changeMode);
    }

    @Override
    public void updateBeforeRender() {
        super.updateBeforeRender();
        this.mode = this.getMenu().mode;
        this.data = this.getMenu().data;
        this.changeMode.setMessage(Component.translatable("gui.extendedae.config_modifier.mode." + this.mode.getSerializedName()));
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.dataInput.isMouseOver(xCoord, yCoord)) {
            this.dataInput.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public @NotNull ActionMap getActionMap() {
        return this.actions;
    }

}
