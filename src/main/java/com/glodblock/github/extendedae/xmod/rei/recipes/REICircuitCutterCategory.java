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

public class REICircuitCutterCategory implements DisplayCategory<REICircuitCutterDisplay> {

    private static final int PADDING = 5;

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EAESingletons.CIRCUIT_CUTTER);
    }

    @Override
    public Component getTitle() {
        return EAESingletons.CIRCUIT_CUTTER.getName();
    }

    @Override
    public CategoryIdentifier<REICircuitCutterDisplay> getCategoryIdentifier() {
        return REICircuitCutterDisplay.ID;
    }

    @Override
    public List<Widget> setupDisplay(REICircuitCutterDisplay recipeDisplay, Rectangle bounds) {
        ResourceLocation location = AppEng.makeId("textures/guis/circuit_cutter.png");

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(location, bounds.x + PADDING, bounds.y + PADDING, 43, 32, 94, 26));
        var input = recipeDisplay.getInputEntries().getFirst();
        var output = recipeDisplay.getOutputEntries().getFirst();

        widgets.add(Widgets.createSlot(new Point(bounds.x + 3 + PADDING, bounds.y + 5 + PADDING)).disableBackground().markInput().entries(input));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 66 + PADDING,  bounds.y + 5 + PADDING)).disableBackground().markOutput().entries(output));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 26 + 2 * PADDING;
    }

    @Override
    public int getDisplayWidth(REICircuitCutterDisplay display) {
        return 94 + 2 * PADDING;
    }

}
