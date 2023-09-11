package com.github.glodblock.epp.xmod.jei;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import appeng.items.misc.WrappedGenericStack;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.client.gui.pattern.GuiPattern;
import com.github.glodblock.epp.container.pattern.ContainerPattern;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private IJeiRuntime jeiRuntime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return EPP.id("jei_plugin");
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
    }

}
