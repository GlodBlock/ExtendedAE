package com.github.glodblock.extendedae.mixins;

import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.core.sync.packets.PatternAccessTerminalPacket;
import com.github.glodblock.extendedae.client.gui.GuiExPatternTerminal;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternAccessTerminalPacket.class)
public abstract class MixinPatternAccessTerminalPacket {

    @Shadow(remap = false)
    private boolean fullUpdate;
    @Shadow(remap = false)
    private long inventoryId;
    @Shadow(remap = false)
    private int inventorySize;
    @Shadow(remap = false)
    private long sortBy;
    @Shadow(remap = false)
    private PatternContainerGroup group;
    @Shadow(remap = false)
    private Int2ObjectMap<ItemStack> slots;

    @Inject(
            method = "clientPacketData",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void handleExGui(Player player, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal patternAccessTerminal) {
            if (fullUpdate) {
                patternAccessTerminal.postFullUpdate(this.inventoryId, sortBy, group, inventorySize, slots);
            } else {
                patternAccessTerminal.postIncrementalUpdate(this.inventoryId, slots);
            }
            ci.cancel();
        }
    }

}
