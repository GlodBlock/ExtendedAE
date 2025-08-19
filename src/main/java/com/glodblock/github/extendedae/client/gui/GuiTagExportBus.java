package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.client.gui.widget.MultilineTextFieldWidget;
import com.glodblock.github.extendedae.container.ContainerTagExportBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class GuiTagExportBus extends UpgradeableScreen<ContainerTagExportBus> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private final SettingToggleButton<RedstoneMode> redstoneMode;

    private MultilineTextFieldWidget filterInputs;
    private MultilineTextFieldWidget filterInputs2;

    private static final Pattern ORE_DICTIONARY_FILTER =
            Pattern.compile("[0-9a-zA-Z* &|^!():/_\\n]*");

    public GuiTagExportBus(ContainerTagExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        this.actions.put("init", o -> {
            if (this.filterInputs != null)  this.filterInputs.setValue(o.get(0));
            if (this.filterInputs2 != null) this.filterInputs2.setValue(o.get(1));
        });

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
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.filterInputs != null && this.filterInputs.isMouseOver(xCoord, yCoord)) {
            this.filterInputs.setValue("");
        }
        if (btn == 1 && this.filterInputs2 != null && this.filterInputs2.isMouseOver(xCoord, yCoord)) {
            this.filterInputs2.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
    }
}
