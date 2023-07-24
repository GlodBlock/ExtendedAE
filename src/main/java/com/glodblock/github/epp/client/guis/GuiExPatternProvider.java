package com.glodblock.github.epp.client.guis;

import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.epp.container.ContainerExPatternProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class GuiExPatternProvider extends PatternProviderScreen<ContainerExPatternProvider> {

    public GuiExPatternProvider(ContainerExPatternProvider menu, PlayerInventory playerInventory, Text title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

}
