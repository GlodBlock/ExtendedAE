package com.github.glodblock.extendedae.xmod.jei;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.integration.modules.jei.ChargerCategory;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import appeng.items.misc.WrappedGenericStack;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.client.gui.GuiExInscriber;
import com.github.glodblock.extendedae.client.gui.pattern.GuiPattern;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.container.pattern.ContainerPattern;
import com.github.glodblock.extendedae.util.Ae2ReflectClient;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private IJeiRuntime jeiRuntime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return EAE.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(GuiPattern.class,
                new IGuiContainerHandler<GuiPattern<?>>() {
                    @Override
                    public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(@NotNull GuiPattern<?> screen, double mouseX, double mouseY) {
                        var stackWithBounds = screen.getSlotUnderMouse();
                        if (stackWithBounds instanceof ContainerPattern.DisplayOnlySlot dpSlot) {
                            var genStack = dpSlot.getItem();
                            if (!genStack.isEmpty()) {
                                var item = genStack.getItem();
                                var key = item instanceof WrappedGenericStack wgs
                                        ? wgs.unwrapWhat(genStack) : AEItemKey.of(genStack);
                                var amount = item instanceof WrappedGenericStack wgs
                                        ? wgs.unwrapAmount(genStack) : dpSlot.getActualAmount();
                                if (key != null && amount > 0) {
                                    var ing = GenericEntryStackHelper.stackToIngredient(jeiRuntime.getIngredientManager(), new GenericStack(key, amount));
                                    var area = new Rect2i(screen.getGuiLeft() + dpSlot.x, screen.getGuiTop() + dpSlot.y, 16, 16);
                                    if (ing != null) {
                                        return Optional.of(new IClickableIngredient<>() {
                                            @Override
                                            @SuppressWarnings({"rawtypes", "unchecked"})
                                            public @NotNull ITypedIngredient getTypedIngredient() {
                                                return ing;
                                            }

                                            @Override
                                            public @NotNull Rect2i getArea() {
                                                return area;
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        return Optional.empty();
                    }
                }
        );
        registration.addGenericGuiContainerHandler(AEBaseScreen.class,
                new IGuiContainerHandler<AEBaseScreen<?>>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull AEBaseScreen<?> screen, double mouseX, double mouseY) {
                        if (screen instanceof GuiExInscriber) {
                            return Collections.singletonList(
                                    IGuiClickableArea.createBasic(82, 50, 26, 16, Ae2ReflectClient.getInscribeRecipe())
                            );
                        }
                        return Collections.emptyList();
                    }
                }
        );
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(EAEItemAndBlock.EX_INSCRIBER), Ae2ReflectClient.getInscribeRecipe());
        registration.addRecipeCatalyst(new ItemStack(EAEItemAndBlock.EX_CHARGER), ChargerCategory.RECIPE_TYPE);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(EAEItemAndBlock.INFINITY_CELL);
    }

}
