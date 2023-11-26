package com.github.glodblock.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.github.glodblock.extendedae.container.ContainerExDrive;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiExDrive extends AEBaseScreen<ContainerExDrive> {

    public GuiExDrive(ContainerExDrive menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        widgets.addOpenPriorityButton();
    }

}
