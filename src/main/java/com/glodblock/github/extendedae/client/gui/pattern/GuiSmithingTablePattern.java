package com.glodblock.github.extendedae.client.gui.pattern;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.container.pattern.ContainerSmithingTablePattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiSmithingTablePattern extends GuiPattern<ContainerSmithingTablePattern> {

    private static final Identifier BG = AppEng.makeId("textures/guis/smithing_table_pattern.png");

    public GuiSmithingTablePattern(ContainerSmithingTablePattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 66;
    }

    @Override
    protected void extractMenuBackground(@NotNull GuiGraphicsExtractor guiGraphics) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

}
