package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.glodblock.github.extendedae.container.ContainerCircuitCutter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCircuitCutter extends UpgradeableScreen<ContainerCircuitCutter> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;

    public GuiCircuitCutter(ContainerCircuitCutter menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);
        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));
        this.autoExportBtn.set(getMenu().getAutoExport());
    }

}
