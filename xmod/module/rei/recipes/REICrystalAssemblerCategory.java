package com.glodblock.github.extendedae.xmod.rei.recipes;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class REICrystalAssemblerCategory implements DisplayCategory<REICrystalAssemblerDisplay> {

    private static final int PADDING = 5;

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EAESingletons.CRYSTAL_ASSEMBLER);
    }

    @Override
    public Component getTitle() {
        return EAESingletons.CRYSTAL_ASSEMBLER.getName();
    }

    @Override
    public CategoryIdentifier<REICrystalAssemblerDisplay> getCategoryIdentifier() {
        return REICrystalAssemblerDisplay.ID;
    }

    @Override
    public List<Widget> setupDisplay(REICrystalAssemblerDisplay recipeDisplay, Rectangle bounds) {
        ResourceLocation location = AppEng.makeId("textures/guis/crystal_assembler.png");

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(location, bounds.x + PADDING, bounds.y + PADDING, 23, 19, 135, 58));
        var output = recipeDisplay.getOutputEntries().getFirst();
        int x = 3;
        int y = 3;
        for (var input : recipeDisplay.getInputItems()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + x + PADDING, bounds.y + y + PADDING)).disableBackground().markInput().entries(input));
            x += 18;
            if (x >= 18 * 3) {
                y += 18;
                x = 3;
            }
        }
        if (!recipeDisplay.getInputFluid().isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 58 + PADDING, bounds.y + 39 + PADDING)).disableBackground().markInput().entries(recipeDisplay.getInputFluid()));
        }
        widgets.add(Widgets.createSlot(new Point(bounds.x + 107 + PADDING, bounds.y + 21 + PADDING)).disableBackground().markOutput().entries(output));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 58 + 2 * PADDING;
    }

    @Override
    public int getDisplayWidth(REICrystalAssemblerDisplay display) {
        return 135 + 2 * PADDING;
    }

}
