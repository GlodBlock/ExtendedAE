package com.glodblock.github.extendedae.client.gui.widget.panel;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.WidgetContainer;
import com.glodblock.github.extendedae.client.gui.GuiExCraftingTerminal;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class ExPanel implements ICompositeWidget {
    protected final GuiExCraftingTerminal<? extends ContainerExCraftingTerminal> screen;
    protected final ContainerExCraftingTerminal menu;
    protected final WidgetContainer widgets;
    protected boolean visible = false;
    protected int x;
    protected int y;

    public ExPanel(GuiExCraftingTerminal<? extends ContainerExCraftingTerminal> screen, WidgetContainer widgets) {
        this.screen = screen;
        this.menu = screen.getMenu();
        this.widgets = widgets;
    }

    public abstract ItemStack getTabIconItem();

    public abstract Component getTabTooltip();

    @Override
    public void setPosition(Point position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public void setSize(int width, int height) {
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(this.x, this.y, 126, 68);
    }

    @Override
    public final boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected void sendPacket(String id, Object... paras) {
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket(id, paras));
    }

}
