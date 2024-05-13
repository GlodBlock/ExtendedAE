package com.glodblock.github.extendedae.mixins;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.core.network.clientbound.PatternAccessTerminalPacket;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternAccessTerminalPacket.class)
public abstract class MixinPatternAccessTerminalPacket {

    @Final
    @Shadow(remap = false)
    private boolean fullUpdate;
    @Final
    @Shadow(remap = false)
    private long inventoryId;
    @Final
    @Shadow(remap = false)
    private int inventorySize;
    @Final
    @Shadow(remap = false)
    private long sortBy;
    @Final
    @Shadow(remap = false)
    private PatternContainerGroup group;
    @Final
    @Shadow(remap = false)
    private Int2ObjectMap<ItemStack> slots;

    @Inject(
            method = "handleOnClient",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void handleExGui(Player player, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal<?> patternAccessTerminal) {
            if (fullUpdate) {
                patternAccessTerminal.postFullUpdate(this.inventoryId, sortBy, group, inventorySize, slots);
            } else {
                patternAccessTerminal.postIncrementalUpdate(this.inventoryId, slots);
            }
            ci.cancel();
        }
    }

}
