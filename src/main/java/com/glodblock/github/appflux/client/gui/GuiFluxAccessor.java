package com.glodblock.github.appflux.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import com.glodblock.github.appflux.network.AFNetworkHandler;
import com.glodblock.github.appflux.network.CAFGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiFluxAccessor extends AEBaseScreen<ContainerFluxAccessor> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    private final AE2Button fastBtn;
    private final AE2Button slowBtn;
    private boolean fastMode = false;

    public GuiFluxAccessor(ContainerFluxAccessor menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.actions.put("init", o -> setMode(o.getBoolean()));
        AFNetworkHandler.INSTANCE.sendToServer(new CAFGenericPacket("update"));
        this.fastBtn = new AE2Button(
                0, 0, 81, 20,
                Component.translatable("gui.appflux.flux_accessor.fast_on"),
                _ -> {
                    AFNetworkHandler.INSTANCE.sendToServer(new CAFGenericPacket("set", false));
                    this.fastMode = false;
                }
        );
        this.fastBtn.setTooltip(Tooltip.create(Component.translatable("gui.appflux.flux_accessor.warn")));
        this.slowBtn = new AE2Button(
                0, 0, 81, 20,
                Component.translatable("gui.appflux.flux_accessor.fast_off"),
                _ -> {
                    AFNetworkHandler.INSTANCE.sendToServer(new CAFGenericPacket("set", true));
                    this.fastMode = true;
                }
        );
        this.slowBtn.setTooltip(Tooltip.create(Component.translatable("gui.appflux.flux_accessor.warn")));
    }

    private void setMode(boolean mode) {
        this.fastMode = mode;
    }

    @Override
    public void init() {
        super.init();
        this.fastBtn.setPosition(this.leftPos + 20, this.topPos + 20);
        this.slowBtn.setPosition(this.leftPos + 20, this.topPos + 20);
        this.addRenderableWidget(this.fastBtn);
        this.addRenderableWidget(this.slowBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.fastBtn.visible = this.fastMode;
        this.slowBtn.visible = !this.fastMode;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
