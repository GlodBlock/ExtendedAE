package com.glodblock.github.extendedae.util;

import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.recipes.handlers.InscriberRecipe;
import com.glodblock.github.extendedae.xmod.LoadList;
import com.glodblock.github.glodium.reflect.ReflectKit;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Ae2ReflectClient {

    private static final Field fInscriberRecipeCategory_RECIPE_TYPE;
    private static final Field fInscriberRecipeCategory_ID;
    private static final Field fNumberEntryWidget_buttons;
    private static final Constructor<?> cFakeForwardingServerLevel;

    static {
        try {
            cFakeForwardingServerLevel = Class
                    .forName("appeng.client.guidebook.scene.element.FakeForwardingServerLevel")
                    .getDeclaredConstructor(LevelAccessor.class);
            cFakeForwardingServerLevel.setAccessible(true);
            if (LoadList.JEI) {
                fInscriberRecipeCategory_RECIPE_TYPE = ReflectKit.reflectField(
                        Class.forName("appeng.integration.modules.jei.InscriberRecipeCategory"),
                        "RECIPE_TYPE"
                );
            } else {
                fInscriberRecipeCategory_RECIPE_TYPE = null;
            }
            if (LoadList.REI) {
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

    public static ServerLevelAccessor getFakeServerWorld(LevelAccessor world) {
        try {
            return (ServerLevelAccessor) cFakeForwardingServerLevel.newInstance(world);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RecipeType<InscriberRecipe> getInscribeRecipe() {
        return ReflectKit.readField(null, fInscriberRecipeCategory_RECIPE_TYPE);
    }

    public static CategoryIdentifier<?> getInscribeRecipeREI() {
        return ReflectKit.readField(null, fInscriberRecipeCategory_ID);
    }

    public static List<Button> getButton(NumberEntryWidget owner) {
        return ReflectKit.readField(owner, fNumberEntryWidget_buttons);
    }

}
