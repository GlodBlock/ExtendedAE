package com.glodblock.github.extendedae.client.gui.pattern;

import appeng.core.AppEng;
import appeng.core.localization.ButtonToolTips;
import com.glodblock.github.extendedae.container.pattern.ContainerCraftingPattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiCraftingPattern extends GuiPattern<ContainerCraftingPattern> {

    private static final Identifier BG = AppEng.makeId("textures/guis/crafting_pattern_recipe.png");

    public GuiCraftingPattern(ContainerCraftingPattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 109;
    }

    @Override
    protected void extractMenuBackground(@NotNull GuiGraphicsExtractor guiGraphics) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.text(
                this.font,
                Component.translatable("gui.pattern_view.craft.substitute", this.menu.canSubstitute() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                8,
                6,
                0x303030,
                false
        );
        guiGraphics.text(
                this.font,
                Component.translatable("gui.pattern_view.craft.fluid_substitute", this.menu.canSubstituteFluids() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                8,
                19,
                0x303030,
                false
        );
    }

}
