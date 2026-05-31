package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.AccessRestriction;
import appeng.api.config.ActionItems;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.client.gui.widget.MultilineTextFieldWidget;
import com.glodblock.github.extendedae.container.ContainerTagStorageBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.regex.Pattern;

public class GuiTagStorageBus extends UpgradeableScreen<ContainerTagStorageBus> {

    private final SettingToggleButton<AccessRestriction> rwMode;
    private final SettingToggleButton<StorageFilter> storageFilter;
    private final SettingToggleButton<YesNo> filterOnExtract;

    private final MultilineTextFieldWidget filterInputs;
    private final MultilineTextFieldWidget filterInputs2;

    private static final Pattern ORE_DICTIONARY_FILTER = Pattern.compile("[0-9a-z* &|^!():/_.\\n]*");

    public GuiTagStorageBus(ContainerTagStorageBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.addOpenPriorityButton();
        addToLeftToolbar(new ActionButton(ActionItems.COG, _ -> menu.partition()));
        this.rwMode = new ServerSettingToggleButton<>(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.storageFilter = new ServerSettingToggleButton<>(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.filterOnExtract = new ServerSettingToggleButton<>(Settings.FILTER_ON_EXTRACT, YesNo.YES);
        this.addToLeftToolbar(this.storageFilter);
        this.addToLeftToolbar(this.filterOnExtract);
        this.addToLeftToolbar(this.rwMode);

        this.filterInputs = new MultilineTextFieldWidget(this.font, 0, 0, 160, 26, Component.translatable("gui.extendedae.tag_storage_bus.whitelist.tooltip"));
        this.filterInputs.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs.setMaxLength(1024);
        this.filterInputs.setValue(menu.exp);
        this.filterInputs.setResponder(s -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", s, true)));

        this.filterInputs2 = new MultilineTextFieldWidget(this.font, 0, 0, 160, 26, Component.translatable("gui.extendedae.tag_storage_bus.blacklist.tooltip"));
        this.filterInputs2.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs2.setMaxLength(1024);
        this.filterInputs2.setValue(menu.exp2);
        this.filterInputs2.setResponder(s -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", s, false)));

        this.widgets.add("filter_input", this.filterInputs);
        this.widgets.add("filter_input_2", this.filterInputs2);

        setInitialFocus(this.filterInputs);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 1 && this.filterInputs.isMouseOver(event.x(), event.y())) {
            this.filterInputs.setValue("");
        }
        if (event.button() == 1 && this.filterInputs2.isMouseOver(event.x(), event.y())) {
            this.filterInputs2.setValue("");
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.storageFilter.set(this.menu.getStorageFilter());
        this.rwMode.set(this.menu.getReadWriteMode());
        this.filterOnExtract.set(this.menu.getFilterOnExtract());
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(10, 17);
        poseStack.scale(0.6f, 0.6f);
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        if (menu.getConnectedTo() != null) {
            guiGraphics.text(font, GuiText.AttachedTo.text(menu.getConnectedTo()), 0, 0, color.toARGB(), false);
        } else {
            guiGraphics.text(font, GuiText.Unattached.text(), 0, 0, color.toARGB(), false);
        }
        poseStack.popMatrix();
    }

}
