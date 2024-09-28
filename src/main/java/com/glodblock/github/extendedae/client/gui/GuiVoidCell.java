package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.api.VoidMode;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.container.ContainerVoidCell;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiVoidCell extends AEBaseScreen<ContainerVoidCell> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private final ActionEPPButton trash;
    private final ActionEPPButton matterBall;
    private final ActionEPPButton singularity;
    private VoidMode mode = VoidMode.TRASH;

    public GuiVoidCell(ContainerVoidCell menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.trash = new ActionEPPButton(b -> {EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", VoidMode.TRASH.ordinal())); this.mode = VoidMode.TRASH;}, Icon.CONDENSER_OUTPUT_TRASH);
        this.matterBall = new ActionEPPButton(b -> {EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", VoidMode.MATTER_BALLS.ordinal())); this.mode = VoidMode.MATTER_BALLS;}, Icon.CONDENSER_OUTPUT_MATTER_BALL);
        this.singularity = new ActionEPPButton(b -> {EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", VoidMode.SINGULARITY.ordinal())); this.mode = VoidMode.SINGULARITY;}, Icon.CONDENSER_OUTPUT_SINGULARITY);
        this.actions.put("init", o -> setMode(o.get(0)));
        EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("update"));
    }

    private void setMode(int modeId) {
        this.mode = VoidMode.values()[modeId];
    }

    @Override
    public void init() {
        super.init();
        this.trash.setPosition(this.leftPos + 22, this.topPos + 20);
        this.matterBall.setPosition(this.leftPos + 54, this.topPos + 20);
        this.singularity.setPosition(this.leftPos + 84, this.topPos + 20);
        this.addRenderableWidget(this.trash);
        this.addRenderableWidget(this.matterBall);
        this.addRenderableWidget(this.singularity);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.extendedae.void_cell.mode." + this.mode.ordinal()),
                5,
                42,
                textColor,
                false
        );
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
