package com.glodblock.github.extendedae.xmod.emi;

import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addGenericStackProvider(new PatternSlotProvider());
        registry.addWorkstation(Ae2ReflectClient.getInscribeRecipe(), EmiStack.of(EAEItemAndBlock.EX_INSCRIBER));
        registry.addWorkstation(Ae2ReflectClient.getChargerRecipe(), EmiStack.of(EAEItemAndBlock.EX_CHARGER));
        registry.setDefaultComparison(EAEItemAndBlock.INFINITY_CELL,
                Comparison.compareData(s -> EAEItemAndBlock.INFINITY_CELL.getRecord(s.getItemStack()))
        );
    }

}
