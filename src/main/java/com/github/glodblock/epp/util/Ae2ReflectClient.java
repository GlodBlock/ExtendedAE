package com.github.glodblock.epp.util;

import appeng.recipes.handlers.InscriberRecipe;
import com.github.glodblock.epp.xmod.LoadList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Ae2ReflectClient {

    private static final Field fLevelRenderer_renderBuffers;
    private static final Field fInscriberRecipeCategory_RECIPE_TYPE;
    private static final Field fInscriberRecipeCategory_ID;
    private static final Constructor<?> cFakeForwardingServerLevel;

    static {
        try {
            fLevelRenderer_renderBuffers = Ae2Reflect.reflectField(LevelRenderer.class, "renderBuffers", "f_109464_");
            cFakeForwardingServerLevel = Class
                    .forName("appeng.client.guidebook.scene.element.FakeForwardingServerLevel")
                    .getDeclaredConstructor(LevelAccessor.class);
            cFakeForwardingServerLevel.setAccessible(true);
            if (LoadList.JEI) {
                fInscriberRecipeCategory_RECIPE_TYPE = Ae2Reflect.reflectField(
                        Class.forName("appeng.integration.modules.jei.InscriberRecipeCategory"),
                        "RECIPE_TYPE"
                );
            } else {
                fInscriberRecipeCategory_RECIPE_TYPE = null;
            }
            if (LoadList.REI) {
                fInscriberRecipeCategory_ID = Ae2Reflect.reflectField(
                        Class.forName("appeng.integration.modules.rei.InscriberRecipeCategory"),
                        "ID"
                );
            } else {
                fInscriberRecipeCategory_ID = null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static RenderBuffers getRenderBuffers(LevelRenderer owner) {
        return Ae2Reflect.readField(owner, fLevelRenderer_renderBuffers);
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
        return Ae2Reflect.readField(null, fInscriberRecipeCategory_RECIPE_TYPE);
    }

    public static CategoryIdentifier<?> getInscribeRecipeREI() {
        return Ae2Reflect.readField(null, fInscriberRecipeCategory_ID);
    }

}
