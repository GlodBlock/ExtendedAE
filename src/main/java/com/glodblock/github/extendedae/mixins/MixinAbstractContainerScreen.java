package com.glodblock.github.extendedae.mixins;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AmountFormat;
import appeng.client.gui.me.common.StackSizeRenderer;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen extends Screen {

    @Inject(
            method = "renderSlotContents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    )
    private void renderMESizeText(GuiGraphicsExtractor graphics, ItemStack itemStack, Slot slot, String itemCount, CallbackInfo ci) {
        if (slot instanceof ContainerPattern.DisplayOnlySlot dpSlot && dpSlot.shouldUseMEText()) {
            long size = dpSlot.getActualAmount();
            if (size > 1) {
                StackSizeRenderer.renderSizeLabel(graphics, this.font, dpSlot.x, dpSlot.y, AEKeyType.items().formatAmount(size, AmountFormat.SLOT));
            }
        }
    }

    private MixinAbstractContainerScreen(Component p_96550_) {
        super(p_96550_);
    }

}
