package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.container.ContainerCaner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiCaner extends AEBaseScreen<ContainerCaner> {

    public GuiCaner(ContainerCaner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

}
