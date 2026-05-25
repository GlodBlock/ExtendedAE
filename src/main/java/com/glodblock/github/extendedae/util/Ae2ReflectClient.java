package com.glodblock.github.extendedae.util;

import appeng.api.parts.IPart;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.parts.PartPlacement;
import com.glodblock.github.glodium.reflect.ConstructorAccessor;
import com.glodblock.github.glodium.reflect.FieldAccessor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;

import java.util.List;

public class Ae2ReflectClient {

    private static final FieldAccessor fEmiInscriberRecipe_CATEGORY;
    private static final FieldAccessor fEmiChargerRecipe_CATEGORY;
    private static final FieldAccessor fInscriberRecipeCategory_ID;
    private static final FieldAccessor fNumberEntryWidget_buttons;
    private static final ConstructorAccessor cPartPlacementPreviewRenderer;

    static {
        fEmiInscriberRecipe_CATEGORY = FieldAccessor.of("appeng.integration.modules.emi.EmiInscriberRecipe", "CATEGORY");
        fEmiChargerRecipe_CATEGORY = FieldAccessor.of("appeng.integration.modules.emi.EmiChargerRecipe", "CATEGORY");
        fInscriberRecipeCategory_ID = FieldAccessor.of("appeng.integration.modules.rei.InscriberRecipeCategory", "ID");
        fNumberEntryWidget_buttons = FieldAccessor.of(NumberEntryWidget.class, "buttons");
        cPartPlacementPreviewRenderer = ConstructorAccessor.of("appeng.client.hooks.RenderBlockOutlineHook$PartPlacementPreviewRenderer", PartPlacement.Placement.class, IPart.class, Vec3.class);
    }

    /*public static EmiRecipeCategory getInscribeRecipe() {
        return fEmiInscriberRecipe_CATEGORY.get(null);
    }

    public static EmiRecipeCategory getChargerRecipe() {
        return fEmiChargerRecipe_CATEGORY.get(null);
    }

    public static CategoryIdentifier<?> getInscribeRecipeREI() {
        return fInscriberRecipeCategory_ID.get(null);
    }*/

    public static List<Button> getButton(NumberEntryWidget owner) {
        return fNumberEntryWidget_buttons.get(owner);
    }

    public static CustomBlockOutlineRenderer createPartPreviewRender(PartPlacement.Placement placement, IPart part, Vec3 cameraRelativePos) {
        return cPartPlacementPreviewRenderer.create(placement, part, cameraRelativePos);
    }

}
