package com.glodblock.github.extendedae.mixins;

import appeng.client.AEClientboundPacketHandler;
import appeng.core.network.clientbound.ClearPatternAccessTerminalPacket;
import appeng.core.network.clientbound.PatternAccessTerminalPacket;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AEClientboundPacketHandler.class)
public abstract class MixinAEClientboundPacketHandler {

    @Inject(
            method = "handleClearPatternAccessTerminalPacket",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void handleExGuiClear(ClearPatternAccessTerminalPacket packet, Minecraft minecraft, Player player, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal<?> patternAccessTerminal) {
            patternAccessTerminal.clear();
            ci.cancel();
        }
    }

    @Inject(
            method = "handlePatternAccessTerminalPacket",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void handleExGuiUpdate(PatternAccessTerminalPacket packet, Minecraft minecraft, Player player, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal<?> patternAccessTerminal) {
            var fullUpdate = packet.fullUpdate();
            var inventoryId = packet.inventoryId();
            var sortBy = packet.sortBy();
            var group = packet.group();
            var inventorySize = packet.inventorySize();
            var slots = packet.slots();
            if (fullUpdate) {
                patternAccessTerminal.postFullUpdate(inventoryId, sortBy, group, inventorySize, slots);
            } else {
                patternAccessTerminal.postIncrementalUpdate(inventoryId, slots);
            }
            ci.cancel();
        }
    }

}
