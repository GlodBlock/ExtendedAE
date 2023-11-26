package com.github.glodblock.extendedae.client.gui;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import com.github.glodblock.extendedae.client.button.ActionEPPButton;
import com.github.glodblock.extendedae.client.button.CycleEPPButton;
import com.github.glodblock.extendedae.client.button.EPPIcon;
import com.github.glodblock.extendedae.common.tileentities.TileExInscriber;
import com.github.glodblock.extendedae.container.ContainerExInscriber;
import com.github.glodblock.extendedae.network.EPPNetworkHandler;
import com.github.glodblock.extendedae.network.packet.CGenericPacket;
import com.github.glodblock.extendedae.network.packet.CUpdatePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiExInscriber extends UpgradeableScreen<ContainerExInscriber> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> separateSidesBtn;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final ActionEPPButton next;
    private final ActionEPPButton pre;

    public GuiExInscriber(ContainerExInscriber menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);

        this.separateSidesBtn = new ServerSettingToggleButton<>(Settings.INSCRIBER_SEPARATE_SIDES, YesNo.NO);
        this.addToLeftToolbar(separateSidesBtn);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        this.next = new ActionEPPButton(b -> EPPNetworkHandler.INSTANCE.sendToServer(new CUpdatePage(() -> (this.menu.page + 1) % TileExInscriber.MAX_THREAD)), EPPIcon.RIGHT);
        this.pre = new ActionEPPButton(b -> EPPNetworkHandler.INSTANCE.sendToServer(new CUpdatePage(() -> (this.menu.page - 1) % TileExInscriber.MAX_THREAD)), EPPIcon.LEFT);
        this.next.setMessage(Component.translatable("gui.extendedae.ex_inscriber.next"));
        this.pre.setMessage(Component.translatable("gui.extendedae.ex_inscriber.pre"));
        CycleEPPButton stackChange = new CycleEPPButton();
        stackChange.addActionPair(EPPIcon.STACK_1, Component.translatable("gui.extendedae.ex_inscriber.unstackable"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("stack", 64)));
        stackChange.addActionPair(EPPIcon.STACK_64, Component.translatable("gui.extendedae.ex_inscriber.stackable"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("stack", 1)));
        stackChange.setState(this.menu.getStackMode());
        addToLeftToolbar(stackChange);
        addToLeftToolbar(this.next);
        addToLeftToolbar(this.pre);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.separateSidesBtn.set(getMenu().getSeparateSides());
        this.autoExportBtn.set(getMenu().getAutoExport());

        this.next.setVisibility(true);
        this.pre.setVisibility(true);
        if (this.menu.page == 0) {
            this.pre.setVisibility(false);
        }
        if (this.menu.page == TileExInscriber.MAX_THREAD - 1) {
            this.next.setVisibility(false);
        }
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("show"));
        this.menu.showPage();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.extendedae.ex_inscriber.number", this.menu.page + 1),
                8,
                16,
                style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
                false
        );
    }

}
