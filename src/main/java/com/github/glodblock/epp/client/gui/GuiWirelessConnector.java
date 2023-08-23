package com.github.glodblock.epp.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.github.glodblock.epp.client.button.TooltipIcon;
import com.github.glodblock.epp.container.ContainerWirelessConnector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiWirelessConnector extends AEBaseScreen<ContainerWirelessConnector> {

    public static final int PADDING_X = 8;
    public static final int PADDING_Y = 6;
    private final TooltipIcon statusIcon = new TooltipIcon();

    public GuiWirelessConnector(ContainerWirelessConnector menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void init() {
        super.init();
        this.statusIcon.setPosition(this.leftPos + 152, this.topPos + PADDING_Y);
        this.addRenderableOnly(this.statusIcon);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        int len = 12;
        this.statusIcon.setTooltip(Tooltip.create(this.menu.status.getDesc()));
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.wireless_connect.status", this.menu.status.getTranslation()),
                PADDING_X,
                PADDING_Y + len,
                textColor,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.wireless_connect.power", String.format("%.2f", this.menu.powerUse)),
                PADDING_X,
                PADDING_Y + len * 2,
                textColor,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.wireless_connect.channel", this.menu.usedChannel, this.menu.maxChannel),
                PADDING_X,
                PADDING_Y + len * 3,
                textColor,
                false
        );
    }

}
