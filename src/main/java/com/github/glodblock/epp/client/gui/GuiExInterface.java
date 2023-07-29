package com.github.glodblock.epp.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import com.github.glodblock.epp.client.ExSemantics;
import com.github.glodblock.epp.container.ContainerExInterface;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiExInterface extends UpgradeableScreen<ContainerExInterface> {

    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final List<Button> amountButtons = new ArrayList<>();

    public GuiExInterface(ContainerExInterface menu, Inventory playerInventory, Component title,
                           ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        addToLeftToolbar(this.fuzzyMode);

        widgets.addOpenPriorityButton();

        var configSlots = menu.getSlots(ExSemantics.EX_1);
        configSlots.addAll(menu.getSlots(ExSemantics.EX_3));
        int i = 0;
        for (; i < configSlots.size(); i++) {
            var button = new SetAmountButton(btn -> {
                var idx = amountButtons.indexOf(btn);
                var configSlot = configSlots.get(idx);
                menu.openSetAmountMenu(configSlot.getSlotIndex());
            });
            button.setDisableBackground(true);
            button.setMessage(ButtonToolTips.InterfaceSetStockAmount.text());
            widgets.add("amtButton" + (1 + i), button);
            amountButtons.add(button);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));

        var configSlots = menu.getSlots(ExSemantics.EX_1);
        configSlots.addAll(menu.getSlots(ExSemantics.EX_3));
        for (int i = 0; i < amountButtons.size(); i++) {
            var button = amountButtons.get(i);
            var item = configSlots.get(i).getItem();
            button.visible = !item.isEmpty();
        }
    }

    static class SetAmountButton extends IconButton {
        public SetAmountButton(OnPress onPress) {
            super(onPress);
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.PERMISSION_BUILD : Icon.PERMISSION_BUILD_DISABLED;
        }
    }
}