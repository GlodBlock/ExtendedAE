package com.github.glodblock.epp.client.gui.pattern;

import appeng.core.AppEng;
import appeng.core.localization.ButtonToolTips;
import com.github.glodblock.epp.container.pattern.ContainerCraftingPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiCraftingPattern extends GuiPattern<ContainerCraftingPattern> {

    private static final ResourceLocation BG = AppEng.makeId("textures/guis/crafting_pattern_recipe.png");

    public GuiCraftingPattern(ContainerCraftingPattern container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 107;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float f, int x, int y) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.pattern_view.craft.substitute", this.menu.canSubstitute() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                8,
                6,
                0x303030,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("gui.pattern_view.craft.fluid_substitute", this.menu.canSubstituteFluids() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                8,
                19,
                0x303030,
                false
        );
    }

}
