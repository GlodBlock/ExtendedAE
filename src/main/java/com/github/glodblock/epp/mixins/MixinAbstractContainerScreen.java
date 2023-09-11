package com.github.glodblock.epp.mixins;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AmountFormat;
import appeng.client.gui.me.common.StackSizeRenderer;
import com.github.glodblock.epp.container.pattern.ContainerPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen extends Screen {

    @Inject(
            method = "renderSlot",
            at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderItemDecorations (Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    )
    private void renderMESizeText(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (slot instanceof ContainerPattern.DisplayOnlySlot dpSlot && dpSlot.shouldUseMEText()) {
            long size = dpSlot.getActualAmount();
            if (size > 1) {
                StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, dpSlot.x, dpSlot.y,
                        AEKeyType.items().formatAmount(size, AmountFormat.SLOT));
            }
        }
    }

    private MixinAbstractContainerScreen(Component p_96550_) {
        super(p_96550_);
    }

}
