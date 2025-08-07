package com.glodblock.github.extendedae.xmod.jei;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.GuiCircuitCutter;
import com.glodblock.github.extendedae.client.gui.GuiCrystalAssembler;
import com.glodblock.github.extendedae.client.gui.GuiExInscriber;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.extendedae.xmod.jei.recipe.CircuitCutterCategory;
import com.glodblock.github.extendedae.xmod.jei.recipe.CrystalAssemblerCategory;
import com.glodblock.github.extendedae.xmod.jei.recipe.CrystalFixerCategory;
import com.glodblock.github.glodium.util.GlodUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.ChargerCategory;
import tamaized.ae2jeiintegration.integration.modules.jei.categories.InscriberRecipeCategory;

import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ExtendedAE.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var helpers = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new CrystalAssemblerCategory(helpers));
        registry.addRecipeCategories(new CircuitCutterCategory(helpers));
        registry.addRecipeCategories(new CrystalFixerCategory(helpers));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        var manager = Minecraft.getInstance().level.getRecipeManager();
        registry.addRecipes(CrystalAssemblerCategory.RECIPE_TYPE, this.getRecipes(CrystalAssemblerRecipe.TYPE, manager));
        registry.addRecipes(CircuitCutterCategory.RECIPE_TYPE, this.getRecipes(CircuitCutterRecipe.TYPE, manager));
        registry.addRecipes(CrystalFixerCategory.RECIPE_TYPE, this.getRecipes(CrystalFixerRecipe.TYPE, manager));
        registry.addIngredientInfo(EAESingletons.ENTRO_CRYSTAL, Component.translatable("emi.extendedae.desc.entro_crystal"));
        registry.addIngredientInfo(EAESingletons.ENTRO_SEED, Component.translatable("emi.extendedae.desc.entro_seed"));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(EAESingletons.CRYSTAL_ASSEMBLER, CrystalAssemblerCategory.RECIPE_TYPE);
        registry.addRecipeCatalyst(EAESingletons.CIRCUIT_CUTTER, CircuitCutterCategory.RECIPE_TYPE);
        registry.addRecipeCatalyst(EAESingletons.CRYSTAL_FIXER, CrystalFixerCategory.RECIPE_TYPE);
        if (GlodUtil.checkMod(ModConstants.AE_JEI)) {
            registry.addRecipeCatalyst(EAESingletons.EX_CHARGER, ChargerCategory.RECIPE_TYPE);
            registry.addRecipeCatalyst(EAESingletons.EX_INSCRIBER, InscriberRecipeCategory.RECIPE_TYPE);
        }
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registry) {
        registry.addGenericGuiContainerHandler(GuiCrystalAssembler.class,
                new IGuiContainerHandler<GuiCrystalAssembler>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull GuiCrystalAssembler screen, double mouseX, double mouseY) {
                        return List.of(IGuiClickableArea.createBasic(81, 42, 40, 12, CrystalAssemblerCategory.RECIPE_TYPE));
                    }
                }
        );
        registry.addGenericGuiContainerHandler(GuiCircuitCutter.class,
                new IGuiContainerHandler<GuiCircuitCutter>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull GuiCircuitCutter screen, double mouseX, double mouseY) {
                        return List.of(IGuiClickableArea.createBasic(65, 39, 35, 12, CircuitCutterCategory.RECIPE_TYPE));
                    }
                }
        );
        if (GlodUtil.checkMod(ModConstants.AE_JEI)) {
            registry.addGenericGuiContainerHandler(GuiExInscriber.class,
                    new IGuiContainerHandler<GuiExInscriber>() {
                        @Override
                        public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull GuiExInscriber screen, double mouseX, double mouseY) {
                            return List.of(IGuiClickableArea.createBasic(82, 50, 26, 16, InscriberRecipeCategory.RECIPE_TYPE));
                        }
                    }
            );
        }
    }

    private <I extends RecipeInput, T extends Recipe<I>> List<T> getRecipes(RecipeType<T> type, RecipeManager manager) {
        return manager.byType(type).stream().map(RecipeHolder::value).toList();
    }

}
