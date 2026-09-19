package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.extendedae.container.ContainerRenamer;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import guideme.PageAnchor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class GuiRenamer extends AEBaseScreen<ContainerRenamer> {

    private final AETextField renameInputs;

    public GuiRenamer(ContainerRenamer menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.renameInputs = widgets.addTextField("rename_input");
        this.renameInputs.setMaxLength(512);
        this.renameInputs.setPlaceholder(Component.translatable("gui.extendedae.renamer.input"));
        this.renameInputs.setValue(menu.name);
        this.renameInputs.setResponder(s -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", s)));
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.renameInputs.isMouseOver(xCoord, yCoord)) {
            this.renameInputs.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.renameInputs.isFocused() && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.renameInputs.setFocused(false);
            EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", this.renameInputs.getValue()));
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        setInitialFocus(this.renameInputs);
    }

    @Override
    protected PageAnchor getHelpTopic() {
        return null;
    }

}
