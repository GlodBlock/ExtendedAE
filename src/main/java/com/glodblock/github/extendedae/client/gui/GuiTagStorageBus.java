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
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class GuiTagStorageBus extends UpgradeableScreen<ContainerTagStorageBus> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private final SettingToggleButton<AccessRestriction> rwMode;
    private final SettingToggleButton<StorageFilter> storageFilter;
    private final SettingToggleButton<YesNo> filterOnExtract;

    private final MultilineTextFieldWidget filterInputs;
    private final MultilineTextFieldWidget filterInputs2;

    private static final Pattern ORE_DICTIONARY_FILTER =
            Pattern.compile("[0-9a-zA-Z* &|^!():/_\\n]*");

    public GuiTagStorageBus(ContainerTagStorageBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.addOpenPriorityButton();
        addToLeftToolbar(new ActionButton(ActionItems.COG, btn -> menu.partition()));
        this.rwMode = new ServerSettingToggleButton<>(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.storageFilter = new ServerSettingToggleButton<>(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.filterOnExtract = new ServerSettingToggleButton<>(Settings.FILTER_ON_EXTRACT, YesNo.YES);
        this.addToLeftToolbar(this.storageFilter);
        this.addToLeftToolbar(this.filterOnExtract);
        this.addToLeftToolbar(this.rwMode);

        var placeholder = Component.translatable("gui.extendedae.tag_storage_bus.tooltip");

        this.filterInputs = new MultilineTextFieldWidget(
                Minecraft.getInstance().font, 0, 0, 160, 26, placeholder);
        this.filterInputs.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs.setMaxLength(1024);
        this.filterInputs.setResponder(s -> EAENetworkHandler.INSTANCE
                .sendToServer(new CEAEGenericPacket("set", s, true)));

        this.filterInputs2 = new MultilineTextFieldWidget(
                Minecraft.getInstance().font, 0, 0, 160, 26, placeholder);
        this.filterInputs2.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs2.setMaxLength(1024);
        this.filterInputs2.setResponder(s -> EAENetworkHandler.INSTANCE
                .sendToServer(new CEAEGenericPacket("set", s, false)));

        widgets.add("filter_input", this.filterInputs);
        widgets.add("filter_input_2", this.filterInputs2);

        setInitialFocus(this.filterInputs);
        EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("update"));
        this.actions.put("init", o -> {
            this.filterInputs.setValue(o.get(0));
            this.filterInputs2.setValue(o.get(1));
        });
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 1 && this.filterInputs.isMouseOver(x, y)) {
            this.filterInputs.setValue("");
        }
        if (btn == 1 && this.filterInputs2.isMouseOver(x, y)) {
            this.filterInputs2.setValue("");
        }
        return super.mouseClicked(x, y, btn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.storageFilter.set(this.menu.getStorageFilter());
        this.rwMode.set(this.menu.getReadWriteMode());
        this.filterOnExtract.set(this.menu.getFilterOnExtract());
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(10, 17, 0);
        poseStack.scale(0.6f, 0.6f, 1);
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        if (menu.getConnectedTo() != null) {
            guiGraphics.drawString(font, GuiText.AttachedTo.text(menu.getConnectedTo()), 0, 0, color.toARGB(), false);
        } else {
            guiGraphics.drawString(font, GuiText.Unattached.text(), 0, 0, color.toARGB(), false);
        }
        poseStack.popPose();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
