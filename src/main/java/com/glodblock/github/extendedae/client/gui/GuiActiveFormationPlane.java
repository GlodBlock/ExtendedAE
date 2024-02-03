package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiActiveFormationPlane extends UpgradeableScreen<ContainerActiveFormationPlane> {

    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final SettingToggleButton<YesNo> placeMode;

    public GuiActiveFormationPlane(ContainerActiveFormationPlane menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.placeMode = new ServerSettingToggleButton<>(Settings.PLACE_BLOCK, YesNo.YES);
        this.addToLeftToolbar(this.placeMode);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.fuzzyMode);
        widgets.addOpenPriorityButton();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.fuzzyMode.set(this.menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.supportsFuzzyMode());
        this.placeMode.set(this.menu.getPlaceMode());
    }

}
