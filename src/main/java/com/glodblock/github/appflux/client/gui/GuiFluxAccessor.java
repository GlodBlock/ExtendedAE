package com.glodblock.github.appflux.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AECheckbox;
import com.glodblock.github.appflux.api.EnergyIO;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import com.glodblock.github.appflux.network.AFNetworkHandler;
import com.glodblock.github.appflux.network.CAFGenericPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiFluxAccessor extends AEBaseScreen<ContainerFluxAccessor> {

    private final AECheckbox fastModeBtn;
    private final AECheckbox inputBtn;
    private final AECheckbox outputBtn;
    private final AECheckbox ioBtn;

    public GuiFluxAccessor(ContainerFluxAccessor menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.fastModeBtn = this.widgets.addCheckbox(
                "fast_mode_btn",
                Component.translatable("gui.appflux.flux_accessor.fast_mode"),
                this::setFastMode
        );
        this.inputBtn = this.widgets.addCheckbox(
                "input_btn",
                Component.translatable("gui.appflux.flux_accessor.input_mode"),
                () -> this.setIOMode(EnergyIO.INPUT.ordinal())
        );
        this.inputBtn.setRadio(true);
        this.outputBtn = this.widgets.addCheckbox(
                "output_btn",
                Component.translatable("gui.appflux.flux_accessor.output_mode"),
                () -> this.setIOMode(EnergyIO.OUTPUT.ordinal())
        );
        this.outputBtn.setRadio(true);
        this.ioBtn = this.widgets.addCheckbox(
                "io_btn",
                Component.translatable("gui.appflux.flux_accessor.input_output_mode"),
                () -> this.setIOMode(EnergyIO.BOTH.ordinal())
        );
        this.ioBtn.setRadio(true);
    }

    private void setFastMode() {
        AFNetworkHandler.INSTANCE.sendToServer(new CAFGenericPacket("fast_mode", this.fastModeBtn.isSelected()));
    }

    private void setIOMode(int code) {
        AFNetworkHandler.INSTANCE.sendToServer(new CAFGenericPacket("io_mode", code));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.fastModeBtn.setSelected(this.menu.fastMode);
        this.inputBtn.setSelected(this.menu.ioMode == 0);
        this.outputBtn.setSelected(this.menu.ioMode == 1);
        this.ioBtn.setSelected(this.menu.ioMode == 2);
    }

}
