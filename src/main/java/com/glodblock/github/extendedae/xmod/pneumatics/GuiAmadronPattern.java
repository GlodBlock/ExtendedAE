package com.glodblock.github.extendedae.xmod.pneumatics;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.client.gui.pattern.GuiPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiAmadronPattern extends GuiPattern<ContainerAmadronPattern> {

    private static final ResourceLocation BG = AppEng.makeId("textures/guis/stonecutting_pattern.png");

    public GuiAmadronPattern(ContainerAmadronPattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 69;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float f, int x, int y) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

}
