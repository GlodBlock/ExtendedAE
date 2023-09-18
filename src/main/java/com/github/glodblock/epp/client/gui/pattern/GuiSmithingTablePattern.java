package com.github.glodblock.epp.client.gui.pattern;

import appeng.core.AppEng;
import com.github.glodblock.epp.container.pattern.ContainerSmithingTablePattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiSmithingTablePattern extends GuiPattern<ContainerSmithingTablePattern> {

    private static final ResourceLocation BG = AppEng.makeId("textures/guis/smithing_table_pattern.png");

    public GuiSmithingTablePattern(ContainerSmithingTablePattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 64;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float f, int x, int y) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

}
