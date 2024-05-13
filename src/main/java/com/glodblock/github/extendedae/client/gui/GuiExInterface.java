package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.FuzzyMode;
import appeng.api.config.Settings;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CUpdatePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiExInterface extends UpgradeableScreen<ContainerExInterface> {

    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final List<Button> amountButtons = new ArrayList<>();
    private final ActionEPPButton nextPage;
    private final ActionEPPButton prePage;

    public GuiExInterface(ContainerExInterface menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.nextPage = new ActionEPPButton(b -> EAENetworkHandler.INSTANCE.sendToServer(new CUpdatePage(1)), EPPIcon.RIGHT);
        this.prePage = new ActionEPPButton(b -> EAENetworkHandler.INSTANCE.sendToServer(new CUpdatePage(0)), EPPIcon.LEFT);
        this.nextPage.setMessage(Component.translatable("gui.extendedae.ex_interface.next"));
        this.prePage.setMessage(Component.translatable("gui.extendedae.ex_interface.pre"));
        addToLeftToolbar(this.fuzzyMode);
        addToLeftToolbar(this.nextPage);
        addToLeftToolbar(this.prePage);

        widgets.addOpenPriorityButton();

        var configSlots = menu.getSlots(ExSemantics.EX_1);
        configSlots.addAll(menu.getSlots(ExSemantics.EX_3));
        configSlots.addAll(menu.getSlots(ExSemantics.EX_5));
        configSlots.addAll(menu.getSlots(ExSemantics.EX_7));
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
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.extendedae.ex_interface.config", this.menu.page + 1),
                8,
                24,
                style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
                false
        );
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.hasUpgrade(AEItems.FUZZY_CARD));

        this.menu.showPage(this.menu.page);
        this.amountButtons.forEach(s -> s.visible = false);

        if (this.menu.page == 0) {
            this.nextPage.setVisibility(true);
            this.prePage.setVisibility(false);
        } else {
            this.prePage.setVisibility(true);
            this.nextPage.setVisibility(false);
        }

        for (int i = 0; i < 18; i++) {
            var button = amountButtons.get(i + this.menu.page * 18);
            var item = this.menu.getConfigSlots().get(i + this.menu.page * 18).getItem();
            button.visible = !item.isEmpty();
        }
    }

    static class SetAmountButton extends IconButton {
        public SetAmountButton(OnPress onPress) {
            super(onPress);
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.WRENCH : Icon.WRENCH_DISABLED;
        }
    }
}