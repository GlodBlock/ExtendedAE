package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.extendedae.common.items.tools.ItemConfigModifier;
import com.glodblock.github.extendedae.container.ContainerConfigModifier;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.regex.Pattern;

public class GuiConfigModifier extends AEBaseScreen<ContainerConfigModifier> {

    private ItemConfigModifier.ConfigSettings.Mode mode;
    private final AE2Button changeMode;
    private final AETextField dataInput;
    private static final Pattern NUMBER = Pattern.compile("[0-9]*");

    public GuiConfigModifier(ContainerConfigModifier menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.mode = menu.mode;
        this.changeMode = new AE2Button(Component.empty(), _ -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set_mode", this.mode.getNext())));
        this.changeMode.setSize(50, 20);
        this.changeMode.setTooltip(Tooltip.create(Component.translatable("gui.extendedae.config_modifier.change_mode")));
        this.dataInput = widgets.addTextField("data_input");
        this.dataInput.setValue(String.valueOf(menu.data));
        this.dataInput.setMaxLength(15);
        this.dataInput.setFilter(NUMBER.asMatchPredicate());
        this.dataInput.setPlaceholder(Component.translatable("gui.extendedae.config_modifier.data_input"));
        this.dataInput.setResponder(this::syncData);
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
        this.changeMode.setMessage(Component.translatable("gui.extendedae.config_modifier.mode." + this.mode.getSerializedName()));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.dataInput.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            this.dataInput.setValue("");
        }
        return super.mouseClicked(event, doubleClick);
    }

}
