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
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class GuiTagStorageBus extends UpgradeableScreen<ContainerTagStorageBus> implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();
    private final SettingToggleButton<AccessRestriction> rwMode;
    private final SettingToggleButton<StorageFilter> storageFilter;
    private final SettingToggleButton<YesNo> filterOnExtract;

    private MultilineTextFieldWidget filterInputs;
    private MultilineTextFieldWidget filterInputs2;

    private static final Pattern ORE_DICTIONARY_FILTER =
            Pattern.compile("[0-9a-zA-Z* &|^!():/_\\n]*");

    public GuiTagStorageBus(ContainerTagStorageBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.addOpenPriorityButton();
        addToLeftToolbar(new ActionButton(ActionItems.WRENCH, btn -> menu.partition()));
        this.rwMode = new ServerSettingToggleButton<>(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.storageFilter = new ServerSettingToggleButton<>(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.filterOnExtract = new ServerSettingToggleButton<>(Settings.FILTER_ON_EXTRACT, YesNo.YES);
        this.addToLeftToolbar(this.storageFilter);
        this.addToLeftToolbar(this.filterOnExtract);
        this.addToLeftToolbar(this.rwMode);

        this.actions.put("init", o -> {
            if (this.filterInputs != null)  this.filterInputs.setValue(o.get(0));
            if (this.filterInputs2 != null) this.filterInputs2.setValue(o.get(1));
        });

        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
    }

    private MultilineTextFieldWidget createMultiline(int x, int y, int w, int h, boolean first) {
        Font font = Minecraft.getInstance().font;
        var placeholder = Component.translatable("gui.expatternprovider.tag_storage_bus.tooltip");
        var tf = new MultilineTextFieldWidget(font, x, y, w, h, placeholder);
        tf.setFilter(ORE_DICTIONARY_FILTER);
        tf.setMaxLength(512);
        tf.setResponder(s ->
                EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s, first))
        );
        return tf;
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

    @Override
    protected void init() {
        super.init();

        if (this.filterInputs == null || this.filterInputs2 == null) {
            var a = style.getWidget("filter_input");
            var b = style.getWidget("filter_input_2");

            int ax = a.getLeft(), ay = a.getTop(), aw = a.getWidth(), ah = a.getHeight();
            int bx = b.getLeft(), by = b.getTop(), bw = b.getWidth(), bh = b.getHeight();

            this.filterInputs  = createMultiline(this.getGuiLeft() + ax, this.getGuiTop() + ay, aw, ah, true);
            this.filterInputs2 = createMultiline(this.getGuiLeft() + bx, this.getGuiTop() + by, bw, bh, false);

            addRenderableWidget(this.filterInputs);
            addRenderableWidget(this.filterInputs2);
        }

        setInitialFocus(this.filterInputs);
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
