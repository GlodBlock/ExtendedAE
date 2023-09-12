package com.github.glodblock.epp.client.gui;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.github.glodblock.epp.client.button.TooltipIcon;
import com.github.glodblock.epp.common.me.wireless.WirelessStatus;
import com.github.glodblock.epp.container.ContainerWirelessConnector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiWirelessConnector extends UpgradeableScreen<ContainerWirelessConnector> {

    public static final int PADDING_X = 8;
    public static final int PADDING_Y = 6;
    private final TooltipIcon statusIcon = new TooltipIcon();

    public GuiWirelessConnector(ContainerWirelessConnector menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void init() {
        super.init();
        this.statusIcon.x = this.leftPos + 152;
        this.statusIcon.y = this.topPos + PADDING_Y;
        this.addRenderableOnly(this.statusIcon);
    }

    @Override
    public void drawFG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        int len = 12;
        this.statusIcon.setMessage(this.menu.status.getDesc());
        this.font.draw(
                poseStack,
                Component.translatable("gui.wireless_connect.status", this.menu.status.getTranslation()),
                PADDING_X,
                PADDING_Y + len,
                textColor
        );
        this.font.draw(
                poseStack,
                Component.translatable("gui.wireless_connect.power", String.format("%.2f", this.menu.powerUse)),
                PADDING_X,
                PADDING_Y + len * 2,
                textColor
        );
        this.font.draw(
                poseStack,
                Component.translatable("gui.wireless_connect.channel", this.menu.usedChannel, this.menu.maxChannel),
                PADDING_X,
                PADDING_Y + len * 3,
                textColor
        );
        if (this.menu.status == WirelessStatus.WORKING) {
            var pos = BlockPos.of(this.menu.otherSide);
            this.font.draw(
                    poseStack,
                    Component.translatable("gui.wireless_connect.remote", pos.getX(), pos.getY(), pos.getZ()),
                    PADDING_X,
                    PADDING_Y + len * 4,
                    textColor
            );
        }
    }

}
