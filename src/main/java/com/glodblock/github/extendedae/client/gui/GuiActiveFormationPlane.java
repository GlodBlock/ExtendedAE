package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiActiveFormationPlane extends UpgradeableScreen<ContainerActiveFormationPlane> {

    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final SettingToggleButton<YesNo> placeMode;
    private final SettingToggleButton<YesNo> craftMode;
    private final SettingToggleButton<RedstoneMode> redstoneMode;

    public GuiActiveFormationPlane(ContainerActiveFormationPlane menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.placeMode = new ServerSettingToggleButton<>(Settings.PLACE_BLOCK, YesNo.YES);
        this.addToLeftToolbar(this.placeMode);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.fuzzyMode);
        this.craftMode = new ServerSettingToggleButton<>(Settings.CRAFT_ONLY, YesNo.NO);
        addToLeftToolbar(this.craftMode);
        widgets.addOpenPriorityButton();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(this.menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.fuzzyMode.set(this.menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.supportsFuzzyMode());
        this.placeMode.set(this.menu.getPlaceMode());
        this.craftMode.set(menu.getCraftingMode());
        this.craftMode.setVisibility(menu.hasUpgrade(AEItems.CRAFTING_CARD));
    }

}
