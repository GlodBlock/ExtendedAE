package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.extendedae.container.ContainerRenamer;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import guideme.PageAnchor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GuiRenamer extends AEBaseScreen<ContainerRenamer> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private final AETextField renameInputs;

    public GuiRenamer(ContainerRenamer menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.renameInputs = widgets.addTextField("rename_input");
        this.renameInputs.setMaxLength(512);
        this.renameInputs.setPlaceholder(Component.translatable("gui.extendedae.renamer.input"));
        this.renameInputs.setResponder(s -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", s)));
        this.actions.put("init", o -> this.renameInputs.setValue(o.getString()));
        EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("update"));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean btn) {
        if (this.renameInputs.isMouseOver(event.x(), event.y()) && event.button() == 1) {
            this.renameInputs.setValue("");
        }
        return super.mouseClicked(event, btn);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (this.renameInputs.isFocused() && event.key() == GLFW.GLFW_KEY_ENTER) {
            this.renameInputs.setFocused(false);
            EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", this.renameInputs.getValue()));
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
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

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
