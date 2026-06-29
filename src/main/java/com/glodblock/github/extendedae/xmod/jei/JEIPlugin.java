package com.glodblock.github.extendedae.xmod.jei;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.client.AppEngClient;
import appeng.client.integrations.jei.ChargerCategory;
import appeng.client.integrations.jei.GenericEntryStackHelper;
import appeng.items.misc.WrappedGenericStack;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.GuiCircuitCutter;
import com.glodblock.github.extendedae.client.gui.GuiCrystalAssembler;
import com.glodblock.github.extendedae.client.gui.pattern.GuiPattern;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.*;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.xmod.jei.recipe.CircuitCutterCategory;
import com.glodblock.github.extendedae.xmod.jei.recipe.CrystalAssemblerCategory;
import com.glodblock.github.extendedae.xmod.jei.recipe.CrystalFixerCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private IJeiRuntime jeiRuntime;

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public @NotNull Identifier getPluginUid() {
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
        registry.addRecipes(CrystalAssemblerCategory.RECIPE_TYPE, this.getRecipes(CrystalAssemblerRecipe.TYPE));
        registry.addRecipes(CircuitCutterCategory.RECIPE_TYPE, this.getRecipes(CircuitCutterRecipe.TYPE));
        registry.addRecipes(CrystalFixerCategory.RECIPE_TYPE, this.getRecipes(CrystalFixerRecipe.TYPE));
        registry.addIngredientInfo(EAESingletons.ENTRO_CRYSTAL, Component.translatable("emi.extendedae.desc.entro_crystal"));
        registry.addIngredientInfo(List.of(
                EAESingletons.ENTRO_SEED.toStack(),
                EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.toStack(),
                EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.toStack(),
                EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.toStack(),
                EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.toStack()
        ), VanillaTypes.ITEM_STACK, Component.translatable("emi.extendedae.desc.entro_seed"));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
        registry.addCraftingStation(CrystalAssemblerCategory.RECIPE_TYPE, EAESingletons.CRYSTAL_ASSEMBLER);
        registry.addCraftingStation(CircuitCutterCategory.RECIPE_TYPE, EAESingletons.CIRCUIT_CUTTER);
        registry.addCraftingStation(CrystalFixerCategory.RECIPE_TYPE, EAESingletons.CRYSTAL_FIXER);
        registry.addCraftingStation(ChargerCategory.RECIPE_TYPE, EAESingletons.EX_CHARGER);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registry) {
        registry.addGenericGuiContainerHandler(GuiCrystalAssembler.class,
                new IGuiContainerHandler<@NotNull GuiCrystalAssembler>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull GuiCrystalAssembler screen, double mouseX, double mouseY) {
                        return List.of(IGuiClickableArea.createBasic(81, 42, 40, 12, CrystalAssemblerCategory.RECIPE_TYPE));
                    }
                }
        );
        registry.addGenericGuiContainerHandler(GuiCircuitCutter.class,
                new IGuiContainerHandler<@NotNull GuiCircuitCutter>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull GuiCircuitCutter screen, double mouseX, double mouseY) {
                        return List.of(IGuiClickableArea.createBasic(65, 39, 35, 12, CircuitCutterCategory.RECIPE_TYPE));
                    }
                }
        );
        registry.addGenericGuiContainerHandler(GuiPattern.class,
                new IGuiContainerHandler<@NotNull GuiPattern<?>>() {
                    @Override
                    public @NotNull Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(@NotNull IClickableIngredientFactory builder, @NotNull GuiPattern<?> screen, double mouseX, double mouseY) {
                        var stackWithBounds = screen.getHoveredSlot();
                        if (stackWithBounds instanceof ContainerPattern.DisplayOnlySlot dpSlot) {
                            var genStack = dpSlot.getItem();
                            if (!genStack.isEmpty()) {
                                var item = genStack.getItem();
                                var key = item instanceof WrappedGenericStack wgs ? wgs.unwrapWhat(genStack) : AEItemKey.of(genStack);
                                var amount = item instanceof WrappedGenericStack wgs ? wgs.unwrapAmount(genStack) : dpSlot.getActualAmount();
                                if (key != null && amount > 0) {
                                    var area = new Rect2i(screen.getLeftPos() + dpSlot.x, screen.getTopPos() + dpSlot.y, 16, 16);
                                    var ing = GenericEntryStackHelper.stackToIngredient(
                                            jeiRuntime.getIngredientManager(),
                                            new GenericStack(key, amount)
                                    );
                                    if (ing != null) {
                                        return builder.createBuilder(ing).buildWithArea(area);
                                    }
                                }
                            }
                        }
                        return Optional.empty();
                    }
                }
        );
    }

  @Override
  public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
    var helper = registration.getTransferHelper();

    registration.addUniversalRecipeTransferHandler(
        new FakeSlotTransferHandler<>(
            ContainerExInterface.TYPE, ContainerExInterface.class, helper));
    registration.addUniversalRecipeTransferHandler(
        new FakeSlotTransferHandler<>(
            ContainerExIOBus.EXPORT_TYPE, ContainerExIOBus.class, helper));
    registration.addUniversalRecipeTransferHandler(
        new FakeSlotTransferHandler<>(
            ContainerThresholdExportBus.TYPE, ContainerThresholdExportBus.class, helper));
    registration.addUniversalRecipeTransferHandler(
        new FakeSlotTransferHandler<>(
            ContainerPreciseExportBus.TYPE, ContainerPreciseExportBus.class, helper));
    registration.addUniversalRecipeTransferHandler(
        new FakeSlotTransferHandler<>(
            ContainerPreciseStorageBus.TYPE, ContainerPreciseStorageBus.class, helper));
  }

    private <I extends RecipeInput, T extends Recipe<@NotNull I>> List<RecipeHolder<@NotNull T>> getRecipes(RecipeType<@NotNull T> type) {
        var recipes = AppEngClient.instance().getRecipeMapForType(Minecraft.getInstance().level, type);
        return List.copyOf(recipes.byType(type));
    }

}
