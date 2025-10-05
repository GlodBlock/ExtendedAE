package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.glodblock.github.extendedae.container.ContainerSmartAnnihilationPlane;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiSmartAnnihilationPlane extends UpgradeableScreen<ContainerSmartAnnihilationPlane> {

    private final SettingToggleButton<FuzzyMode> fuzzyMode;

    public GuiSmartAnnihilationPlane(ContainerSmartAnnihilationPlane menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.fuzzyMode);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.fuzzyMode.set(this.menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.supportsFuzzyMode());
    }

}
