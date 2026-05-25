package com.glodblock.github.extendedae.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.parts.AEBasePart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AEBasePart.class)
public abstract class MixinAEBasePart {

    @Final
    @Shadow(remap = false)
    private IManagedGridNode mainNode;

    @Shadow(remap = false)
    public abstract Level getLevel();

    @Shadow(remap = false)
    private BlockEntity blockEntity;

    @Redirect(
            method = "readFromNBT",
            at = @At(value = "INVOKE", target = "Lappeng/api/networking/IManagedGridNode;deserialize(Lnet/minecraft/world/level/storage/ValueInput;)V"),
            remap = false
    )
    private void bypassNodeLoad(IManagedGridNode node, ValueInput tag) {
        if (!tag.getBooleanOr("BYPASS_EXTENDEDAE", false)) {
            node.deserialize(tag);
        }
    }

    /**
     * @author GlodBlock
     * @reason Stop throwing Exception
     */
    @Overwrite(remap = false)
    public void addToWorld() {
        try {
            this.mainNode.create(this.getLevel(), this.blockEntity.getBlockPos());
        } catch (IllegalStateException ignored) {
            // NO-OP
        }
    }

}
