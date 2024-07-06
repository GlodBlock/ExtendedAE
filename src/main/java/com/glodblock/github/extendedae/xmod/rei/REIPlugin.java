package com.glodblock.github.extendedae.xmod.rei;

import appeng.api.integrations.rei.IngredientConverters;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.itemlists.CompatLayerHelper;
import appeng.integration.modules.rei.ChargerDisplay;
import appeng.items.misc.WrappedGenericStack;
import com.glodblock.github.extendedae.client.gui.GuiCircuitCutter;
import com.glodblock.github.extendedae.client.gui.GuiCrystalAssembler;
import com.glodblock.github.extendedae.client.gui.GuiExInscriber;
import com.glodblock.github.extendedae.client.gui.pattern.GuiPattern;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import com.glodblock.github.extendedae.xmod.rei.recipes.REICircuitCutterCategory;
import com.glodblock.github.extendedae.xmod.rei.recipes.REICircuitCutterDisplay;
import com.glodblock.github.extendedae.xmod.rei.recipes.REICrystalAssemblerCategory;
import com.glodblock.github.extendedae.xmod.rei.recipes.REICrystalAssemblerDisplay;
import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return "ExtendedAE";
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.registerFocusedStack((screen, mouse) -> {
            if (screen instanceof GuiPattern<?> patternScreen) {
                var stackWithBounds = patternScreen.getSlotUnderMouse();
                if (stackWithBounds instanceof ContainerPattern.DisplayOnlySlot dpSlot) {
                    var genStack = dpSlot.getItem();
                    if (!genStack.isEmpty()) {
                        var item = genStack.getItem();
                        var key = item instanceof WrappedGenericStack wgs
                                ? wgs.unwrapWhat(genStack) : AEItemKey.of(genStack);
                        var amount = item instanceof WrappedGenericStack wgs
                                ? wgs.unwrapAmount(genStack) : dpSlot.getActualAmount();
                        if (key != null && amount > 0) {
                            var stack = new GenericStack(key, amount);
                            for (var converter : IngredientConverters.getConverters()) {
                                var entryStack = converter.getIngredientFromStack(stack);
                                if (entryStack != null) {
                                    return CompoundEventResult.interruptTrue(entryStack);
                                }
                            }
                        }
                    }
                }
            }
            return CompoundEventResult.pass();
        });
        registry.registerContainerClickArea(
                new Rectangle(82, 50, 26, 16),
                GuiExInscriber.class,
                Ae2ReflectClient.getInscribeRecipeREI());
        registry.registerContainerClickArea(
                new Rectangle(65, 39, 35, 12),
                GuiCircuitCutter.class,
                REICircuitCutterDisplay.ID);
        registry.registerContainerClickArea(
                new Rectangle(81, 42, 40, 12),
                GuiCrystalAssembler.class,
                REICrystalAssemblerDisplay.ID);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.registerRecipeFiller(CircuitCutterRecipe.class, CircuitCutterRecipe.TYPE, REICircuitCutterDisplay::new);
        registry.registerRecipeFiller(CrystalAssemblerRecipe.class, CrystalAssemblerRecipe.TYPE, REICrystalAssemblerDisplay::new);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.addWorkstations(ChargerDisplay.ID, EntryStacks.of(EAESingletons.EX_CHARGER));
        registry.addWorkstations(Ae2ReflectClient.getInscribeRecipeREI(), EntryStacks.of(EAESingletons.EX_INSCRIBER));
        registry.add(new REICircuitCutterCategory());
        registry.addWorkstations(REICircuitCutterDisplay.ID, EntryStacks.of(EAESingletons.CIRCUIT_CUTTER));
        registry.add(new REICrystalAssemblerCategory());
        registry.addWorkstations(REICrystalAssemblerDisplay.ID, EntryStacks.of(EAESingletons.CRYSTAL_ASSEMBLER));
    }

}
