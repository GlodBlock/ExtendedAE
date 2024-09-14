package com.glodblock.github.extendedae.mixins;

import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.proxy.DefaultProxyProvider;
import blusunrize.immersiveengineering.common.wires.WireSyncManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GlobalWireNetwork.class)
public abstract class MixinGlobalWireNetwork {

    @Redirect(
            method = "getNetwork",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getCapability(Lnet/minecraftforge/common/capabilities/Capability;)Lnet/minecraftforge/common/util/LazyOptional;"),
            remap = false
    )
    private static LazyOptional<GlobalWireNetwork> addEmptyHandler(Level world, Capability<GlobalWireNetwork> capability) {
        var op = world.getCapability(capability);
        if (op.isPresent()) {
            return op;
        }
        return LazyOptional.of(() -> new GlobalWireNetwork(world.isClientSide, new DefaultProxyProvider(world), new WireSyncManager(world)));
    }

}
