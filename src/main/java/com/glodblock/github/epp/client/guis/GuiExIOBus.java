package com.glodblock.github.epp.client.guis;

import appeng.api.config.*;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.glodblock.github.epp.container.ContainerExIOBus;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiExIOBus extends UpgradeableScreen<ContainerExIOBus> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final SettingToggleButton<YesNo> craftMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;

    public GuiExIOBus(ContainerExIOBus menu, PlayerInventory playerInventory, Text title,
                      ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE,
                FuzzyMode.IGNORE_ALL);
        addToLeftToolbar(this.fuzzyMode);

        if (menu.getHost().getConfigManager().hasSetting(Settings.CRAFT_ONLY)) {
            this.craftMode = new ServerSettingToggleButton<>(Settings.CRAFT_ONLY, YesNo.NO);
            addToLeftToolbar(this.craftMode);
        } else {
            this.craftMode = null;
        }

        if (menu.getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(handler.getRedStoneMode());
        this.redstoneMode.setVisibility(handler.hasUpgrade(AEItems.REDSTONE_CARD));
        this.fuzzyMode.set(handler.getFuzzyMode());
        this.fuzzyMode.setVisibility(handler.hasUpgrade(AEItems.FUZZY_CARD));
        if (this.craftMode != null) {
            this.craftMode.set(handler.getCraftingMode());
            this.craftMode.setVisibility(handler.hasUpgrade(AEItems.CRAFTING_CARD));
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.set(handler.getSchedulingMode());
        }
    }

}
