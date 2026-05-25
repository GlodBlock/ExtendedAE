package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.container.ContainerIngredientBuffer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiIngredientBuffer extends AEBaseScreen<ContainerIngredientBuffer> {

    public GuiIngredientBuffer(ContainerIngredientBuffer menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

}
