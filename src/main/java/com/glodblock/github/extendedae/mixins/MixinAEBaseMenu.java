package com.glodblock.github.extendedae.mixins;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.container.ContainerExCraftingTerminal;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AEBaseMenu.class)
public abstract class MixinAEBaseMenu {

    @Final
    @Shadow(remap = false)
    private Map<Slot, SlotSemantic> semanticBySlot;

    @Inject(
            method = "isPlayerSideSlot",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void checkPlayerSide(Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof ContainerExCraftingTerminal) {
            var sem = this.semanticBySlot.get(slot);
            if (sem == SlotSemantics.CRAFTING_GRID ||
                sem == SlotSemantics.STONECUTTING_INPUT ||
                sem == SlotSemantics.SMITHING_TABLE_TEMPLATE ||
                sem == SlotSemantics.SMITHING_TABLE_BASE ||
                sem == SlotSemantics.SMITHING_TABLE_ADDITION ||
                sem == ExSemantics.EX_2 ||
                sem == ExSemantics.EX_3) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

}
