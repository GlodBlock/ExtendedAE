package com.github.glodblock.epp.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import com.github.glodblock.epp.container.ContainerTagExportBus;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class GuiTagExportBus extends UpgradeableScreen<ContainerTagExportBus> implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();
    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final AETextField filterInputs;
    private static final Pattern ORE_DICTIONARY_FILTER = Pattern.compile("[0-9a-zA-Z* &|^!():/_]*");

    public GuiTagExportBus(ContainerTagExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);
        this.filterInputs = widgets.addTextField("filter_input");
        this.filterInputs.setFilter(str -> ORE_DICTIONARY_FILTER.matcher(str).matches());
        this.filterInputs.setMaxLength(512);
        this.filterInputs.setPlaceholder(Component.translatable("gui.expatternprovider.tag_storage_bus.tooltip"));
        this.filterInputs.setResponder(s -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s)));
        this.actions.put("init", o -> this.filterInputs.setValue(o.get(0)));
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.filterInputs.isMouseOver(xCoord, yCoord)) {
            this.filterInputs.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
    }

    @Override
    protected void init() {
        super.init();
        setInitialFocus(this.filterInputs);
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);
        this.filterInputs.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

}
