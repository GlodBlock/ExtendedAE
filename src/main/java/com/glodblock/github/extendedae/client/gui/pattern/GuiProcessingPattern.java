package com.glodblock.github.extendedae.client.gui.pattern;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiProcessingPattern extends GuiPattern<ContainerProcessingPattern> {

    private static final Identifier BG = AppEng.makeId("textures/guis/processing_pattern_recipe.png");

    public GuiProcessingPattern(ContainerProcessingPattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 251;
    }

    @Override
    protected void extractMenuBackground(@NotNull GuiGraphicsExtractor guiGraphics) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

}
