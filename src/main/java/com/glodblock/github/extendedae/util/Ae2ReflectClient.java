package com.glodblock.github.extendedae.util;

import appeng.client.gui.widgets.NumberEntryWidget;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.reflect.ReflectKit;
import com.glodblock.github.glodium.util.GlodUtil;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.client.gui.components.Button;

import java.lang.reflect.Field;
import java.util.List;

public class Ae2ReflectClient {

    private static final Field fEmiInscriberRecipe_CATEGORY;
    private static final Field fEmiChargerRecipe_CATEGORY;
    private static final Field fInscriberRecipeCategory_ID;
    private static final Field fNumberEntryWidget_buttons;

    static {
        try {
            if (GlodUtil.checkMod(ModConstants.EMI)) {
                fEmiInscriberRecipe_CATEGORY = ReflectKit.reflectField(
                        Class.forName("appeng.integration.modules.emi.EmiInscriberRecipe"),
                        "CATEGORY"
                );
                fEmiChargerRecipe_CATEGORY = ReflectKit.reflectField(
                        Class.forName("appeng.integration.modules.emi.EmiChargerRecipe"),
                        "CATEGORY"
                );
            } else {
                fEmiInscriberRecipe_CATEGORY = null;
                fEmiChargerRecipe_CATEGORY = null;
            }
            if (GlodUtil.checkMod(ModConstants.REI)) {
                fInscriberRecipeCategory_ID = ReflectKit.reflectField(
                        Class.forName("appeng.integration.modules.rei.InscriberRecipeCategory"),
                        "ID"
                );
            } else {
                fInscriberRecipeCategory_ID = null;
            }
            fNumberEntryWidget_buttons = ReflectKit.reflectField(NumberEntryWidget.class, "buttons");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static EmiRecipeCategory getInscribeRecipe() {
        return ReflectKit.readField(null, fEmiInscriberRecipe_CATEGORY);
    }

    public static EmiRecipeCategory getChargerRecipe() {
        return ReflectKit.readField(null, fEmiChargerRecipe_CATEGORY);
    }

    public static CategoryIdentifier<?> getInscribeRecipeREI() {
        return ReflectKit.readField(null, fInscriberRecipeCategory_ID);
    }

    public static List<Button> getButton(NumberEntryWidget owner) {
        return ReflectKit.readField(owner, fNumberEntryWidget_buttons);
    }

}
