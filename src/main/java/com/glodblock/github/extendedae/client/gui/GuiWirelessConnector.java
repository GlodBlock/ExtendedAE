package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.client.button.HighlightButton;
import com.glodblock.github.extendedae.client.button.TooltipIcon;
import com.glodblock.github.extendedae.client.gui.widget.WorldDisplay;
import com.glodblock.github.extendedae.common.me.wireless.WirelessStatus;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.glodblock.github.extendedae.util.MessageUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.io.IOException;
import java.util.Objects;

public class GuiWirelessConnector extends UpgradeableScreen<ContainerWirelessConnector> {

    public static final int PADDING_X = 8;
    public static final int PADDING_Y = 6;
    private final TooltipIcon statusIcon = new TooltipIcon();
    private final HighlightButton highlight;
    private final WorldDisplay remote;
    private BlockPos lastPos = null;

    public GuiWirelessConnector(ContainerWirelessConnector menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.remote = new WorldDisplay(this, 0, 0, 129, 63);
        this.highlight = new HighlightButton();
        this.highlight.setTooltip(Tooltip.create(Component.translatable("gui.wireless_connect.highlight.tooltip")));
    }

    @Override
    public void init() {
        super.init();
        this.statusIcon.setPosition(this.leftPos + 152, this.topPos + PADDING_Y - 1);
        this.remote.setPosition(this.leftPos + 24, this.topPos + 76);
        this.highlight.setPosition(this.leftPos + 152, this.topPos + PADDING_Y + 21);
        this.remote.refreshBounds();
        this.addRenderableOnly(this.statusIcon);
        this.addRenderableWidget(this.remote);
        this.addRenderableWidget(this.highlight);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        if (this.menu.status == WirelessStatus.WORKING) {
            var remotePos = BlockPos.of(this.menu.otherSide);
            if (!Objects.equals(remotePos, this.lastPos)) {
                this.remote.locate(remotePos);
                this.lastPos = remotePos;
            }
            try (var level = this.getPlayer().level()) {
                this.highlight.setTarget(remotePos, level.dimension());
                this.highlight.setMultiplier(this.playerToBlockDis(remotePos));
                this.highlight.setSuccessJob(() -> {
                    if (this.getPlayer() != null) {
                        Component message = MessageUtil.createEnhancedHighlightMessage(this.getPlayer(), remotePos, level.dimension(), "chat.wireless.highlight");
                        this.getPlayer().sendOverlayMessage(message);
                    }
                });
                this.highlight.setVisibility(true);
            } catch (IOException e) {
                this.highlight.setVisibility(false);
            }
        } else {
            this.remote.unload();
            this.highlight.setVisibility(false);
        }
    }

    private double playerToBlockDis(BlockPos pos) {
        if (pos == null) {
            return 0;
        }
        var ps = this.getPlayer().getOnPos();
        return pos.distSqr(ps);
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        int len = 12;
        this.statusIcon.setTooltip(Tooltip.create(this.menu.status.getDesc()));
        guiGraphics.text(
                this.font,
                Component.translatable("gui.wireless_connect.status", this.menu.status.getTranslation()),
                PADDING_X,
                PADDING_Y + len,
                textColor,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.wireless_connect.power", String.format("%.2f", this.menu.powerUse)),
                PADDING_X,
                PADDING_Y + len * 2,
                textColor,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.wireless_connect.channel", this.menu.usedChannel, this.menu.maxChannel),
                PADDING_X,
                PADDING_Y + len * 3,
                textColor,
                false
        );
        if (this.menu.status == WirelessStatus.WORKING) {
            var pos = BlockPos.of(this.menu.otherSide);
            guiGraphics.text(
                    this.font,
                    Component.translatable("gui.wireless_connect.remote", pos.getX(), pos.getY(), pos.getZ()),
                    22,
                    62,
                    textColor,
                    false
            );
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.remote.mouseDragged(event, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (this.remote.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)) {
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

}
