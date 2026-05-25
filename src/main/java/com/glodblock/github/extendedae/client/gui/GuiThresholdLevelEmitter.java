package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.glodblock.github.extendedae.client.gui.widget.NumberInputField;
import com.glodblock.github.extendedae.container.ContainerThresholdLevelEmitter;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiThresholdLevelEmitter extends UpgradeableScreen<ContainerThresholdLevelEmitter> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final NumberInputField upperLevel;
    private final NumberInputField lowerLevel;

    public GuiThresholdLevelEmitter(ContainerThresholdLevelEmitter menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_EMITTER, RedstoneMode.LOW_SIGNAL);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.redstoneMode);
        this.addToLeftToolbar(this.fuzzyMode);

        this.upperLevel = new NumberInputField(style, NumberEntryType.of(menu.getConfiguredFilter()));
        this.widgets.add("upperLevel", this.upperLevel);
        this.upperLevel.setTextFieldStyle(style.getWidget("upperLevelInput"));
        this.upperLevel.setLongValue(this.menu.upperValue);
        this.upperLevel.setOnChange(this::saveUpperValue);
        this.upperLevel.setOnConfirm(this::onClose);
        this.upperLevel.addRemover(this::removeBtn);

        this.lowerLevel = new NumberInputField(style, NumberEntryType.of(menu.getConfiguredFilter()));
        this.widgets.add("lowerLevel", this.lowerLevel);
        this.lowerLevel.setTextFieldStyle(style.getWidget("lowerLevelInput"));
        this.lowerLevel.setLongValue(this.menu.lowerValue);
        this.lowerLevel.setOnChange(this::saveLowerValue);
        this.lowerLevel.setOnConfirm(this::onClose);
        this.lowerLevel.addRemover(this::removeBtn);
    }

    private void removeBtn(AbstractWidget widget) {
        widget.active = false;
        widget.visible = false;
        this.removeWidget(widget);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.upperLevel.setType(NumberEntryType.of(menu.getConfiguredFilter()));
        this.lowerLevel.setType(NumberEntryType.of(menu.getConfiguredFilter()));

        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.supportsFuzzySearch());

        this.upperLevel.setActive(true);
        this.lowerLevel.setActive(true);

        this.redstoneMode.active = true;
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(true);
    }

    private void saveUpperValue() {
        this.upperLevel.getLongValue().ifPresent(menu::setUpperValue);
    }

    private void saveLowerValue() {
        this.lowerLevel.getLongValue().ifPresent(menu::setLowerValue);
    }

}
