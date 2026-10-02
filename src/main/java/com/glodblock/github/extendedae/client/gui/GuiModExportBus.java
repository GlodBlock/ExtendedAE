package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.container.ContainerModExportBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.extendedae.util.FCClientUtil;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GuiModExportBus extends UpgradeableScreen<ContainerModExportBus> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final AETextField filterInputs;

    public GuiModExportBus(ContainerModExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);
        this.filterInputs = widgets.addTextField("filter_input");
        this.filterInputs.setValue(menu.exp);
        this.filterInputs.setMaxLength(512);
        this.filterInputs.setPlaceholder(Component.translatable("gui.extendedae.mod_storage_bus.tooltip"));
        this.filterInputs.setResponder(s -> {
            this.filterInputs.setSuggestion(FCClientUtil.getModName(s));
            EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", s));
        });
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_TAB && this.filterInputs.isFocused()) {
            var suggest = FCClientUtil.getModName(this.filterInputs.getValue());
            if (!suggest.isEmpty()) {
                this.filterInputs.setValue(this.filterInputs.getValue() + suggest);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean btn) {
        if (this.filterInputs.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            this.filterInputs.setValue("");
        }
        return super.mouseClicked(event, btn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
    }

    @Override
    protected void init() {
        super.init();
        setInitialFocus(this.filterInputs);
    }

}
