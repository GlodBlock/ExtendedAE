package com.glodblock.github.extendedae.xmod.emi.recipes;

import com.glodblock.github.extendedae.ExtendedAE;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.network.chat.Component;

public class EAERecipeCategory extends EmiRecipeCategory {
    private final Component name;

    public EAERecipeCategory(String id, EmiRenderable icon, Component name) {
        super(ExtendedAE.id(id), icon);
        this.name = name;
    }

    @Override
    public Component getName() {
        return name;
    }
}
