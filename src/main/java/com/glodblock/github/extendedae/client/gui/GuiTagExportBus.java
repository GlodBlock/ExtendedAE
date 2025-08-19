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
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class GuiTagExportBus extends UpgradeableScreen<ContainerTagExportBus> implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();
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

        var placeholder = Component.translatable("gui.expatternprovider.tag_storage_bus.tooltip");

        this.filterInputs = new MultilineTextFieldWidget(
                Minecraft.getInstance().font, 0, 0, 160, 26, placeholder);
        this.filterInputs.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs.setMaxLength(1024);
        this.filterInputs.setResponder(s ->
                EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s, true)));

        this.filterInputs2 = new MultilineTextFieldWidget(
                Minecraft.getInstance().font, 0, 0, 160, 26, placeholder);
        this.filterInputs2.setFilter(ORE_DICTIONARY_FILTER);
        this.filterInputs2.setMaxLength(1024);
        this.filterInputs2.setResponder(s ->
                EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s, false)));

        widgets.add("filter_input", this.filterInputs);
        widgets.add("filter_input_2", this.filterInputs2);

        setInitialFocus(this.filterInputs);

        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
    }
}
