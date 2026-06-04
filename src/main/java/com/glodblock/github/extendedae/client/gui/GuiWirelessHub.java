package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.AppEng;
import appeng.util.Icon;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.HighlightButton;
import com.glodblock.github.extendedae.common.me.wireless.WirelessStatus;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.extendedae.util.MessageUtil;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GuiWirelessHub extends UpgradeableScreen<ContainerWirelessHub> {

    private static final Blitter PORT = Blitter.texture(AppEng.makeId("textures/guis/wireless_hub.png")).src(176, 0, 16, 16);
    private static final int COL1_X = 37;
    private static final int COL2_X = 120;
    private static final int COL_Y = 44;
    private static final int Y_OFFSET = 28;
    private static final int PADDING_X = 8;
    private static final int PADDING_Y = 6;
    private final RemoteBlock[] remotes = new RemoteBlock[TileWirelessHub.MAX_PORT];
    private final HighlightButton[] highlightBtn = new HighlightButton[TileWirelessHub.MAX_PORT];
    private final ActionEPPButton[] disconnectBtn = new ActionEPPButton[TileWirelessHub.MAX_PORT];

    public GuiWirelessHub(ContainerWirelessHub menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
            final int port = i;
            this.remotes[i] = new RemoteBlock(() -> menu.getRemotePosition(port), 0, 0, 16, 16);
            this.highlightBtn[i] = new HighlightButton();
            this.highlightBtn[i].setTooltip(Tooltip.create(Component.translatable("gui.wireless_connect.highlight.tooltip")));
            this.disconnectBtn[i] = new ActionEPPButton(_ -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("disconnect", port)), Icon.CLEAR);
            this.disconnectBtn[i].setTooltip(Tooltip.create(Component.translatable("gui.wireless_hub.disconnect.tooltip")));
        }
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
            int X = i < 4 ? COL1_X : COL2_X;
            int Y = i % 4;
            this.remotes[i].setPosition(this.leftPos + X, this.topPos + COL_Y + Y * Y_OFFSET);
            this.highlightBtn[i].setPosition(this.leftPos + X - 18, this.topPos + COL_Y + Y * Y_OFFSET - 2);
            this.disconnectBtn[i].setPosition(this.leftPos + X + 18, this.topPos + COL_Y + Y * Y_OFFSET - 2);
            this.addRenderableOnly(this.remotes[i]);
            this.addRenderableWidget(this.highlightBtn[i]);
            this.addRenderableWidget(this.disconnectBtn[i]);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        var dim = this.getPlayer().level().dimension();
        for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
            var status = this.menu.getStatus(i);
            if (status == WirelessStatus.WORKING || status == WirelessStatus.NO_POWER) {
                var remotePos = this.menu.getRemotePosition(i);
                this.highlightBtn[i].active = true;
                this.highlightBtn[i].setTarget(remotePos, dim);
                this.highlightBtn[i].setMultiplier(this.playerToBlockDis(remotePos));
                this.highlightBtn[i].setSuccessJob(() -> {
                    if (this.getPlayer() != null) {
                        Component message = MessageUtil.createEnhancedHighlightMessage(this.getPlayer(), remotePos, this.getPlayer().level().dimension(), "chat.wireless.highlight");
                        this.getPlayer().sendSystemMessage(message);
                    }
                });
                this.remotes[i].setTooltip(Tooltip.create(Component.translatable("gui.wireless_connect.remote_channel", remotePos.getX(), remotePos.getY(), remotePos.getZ(), this.menu.getRemoteChannel(i))));
                this.remotes[i].setConnected(true);
            } else {
                this.highlightBtn[i].active = false;
                this.remotes[i].setTooltip(Tooltip.create(Component.translatable("gui.wireless_hub.empty_port.tooltip")));
                this.remotes[i].setConnected(false);
            }
        }
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        int len = 12;
        guiGraphics.text(
                this.font,
                Component.translatable("gui.wireless_connect.power", String.format("%.2f", this.menu.powerUse)),
                PADDING_X,
                PADDING_Y + len,
                textColor,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.wireless_connect.channel", this.menu.usedChannel, this.menu.maxChannel),
                PADDING_X,
                PADDING_Y + len * 2,
                textColor,
                false
        );
    }

    private double playerToBlockDis(BlockPos pos) {
        if (pos == null) {
            return 0;
        }
        var ps = this.getPlayer().getOnPos();
        return pos.distSqr(ps);
    }

    private static class RemoteBlock extends AbstractWidget {

        private final Supplier<BlockPos> locator;
        @Nullable
        private BlockPos localPos;
        private ItemStack localBlock = ItemStack.EMPTY;
        private boolean isConnected = false;

        public RemoteBlock(Supplier<BlockPos> locator, int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());
            this.locator = locator;
        }

        private void setConnected(boolean connected) {
            this.isConnected = connected;
        }

        @Override
        protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            if (this.localPos != this.locator.get() && Minecraft.getInstance().level != null) {
                this.localPos = this.locator.get();
                this.localBlock = new ItemStack(Minecraft.getInstance().level.getBlockState(this.localPos).getBlock());
            }
            if (this.isConnected) {
                var renderContext = new SimpleRenderContext(LytRect.empty(), graphics);
                renderContext.renderItem(
                        this.localBlock,
                        this.getX(), this.getY(),
                        16,
                        16);
            } else {
                PORT.dest(this.getX(), this.getY()).blit(graphics);
            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
            // NO-OP
        }

    }

}
